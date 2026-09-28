package com.financialapp.notifications.application.service;

import com.financialapp.notifications.domain.gateway.FinancesGateway;
import com.financialapp.notifications.domain.messaging.EmailSender;
import com.financialapp.notifications.domain.model.category.CategorySummary;
import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.notification.NotificationCategory;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationPreference;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import com.financialapp.notifications.domain.model.notification.UserNotificationPreference;
import com.financialapp.notifications.domain.repository.MonthlySummarySentRepository;
import com.financialapp.notifications.domain.repository.NotificationPreferenceRepository;
import com.financialapp.notifications.domain.service.NotificationService;
import com.financialapp.notifications.domain.usecase.notification.MonthlySummaryResult;
import com.financialapp.notifications.domain.usecase.notification.MonthlySummarySkip;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MonthlySummaryDelivery {

    private static final DateTimeFormatter TITLE_MONTH = DateTimeFormatter.ofPattern("MMMM yyyy");

    private final NotificationPreferenceRepository categoryPreferences;
    private final MonthlySummarySentRepository sentRepository;
    private final FinancesGateway financesGateway;
    private final NotificationService notificationService;
    private final EmailSender emailSender;

    public MonthlySummaryResult deliver(UserNotificationPreference recipient, YearMonth month) {
        if (!summaryEmailEnabled(recipient.userId())) {
            return new MonthlySummaryResult(false, month, MonthlySummarySkip.OPTED_OUT);
        }
        if (!sentRepository.claim(recipient.userId(), month)) {
            return new MonthlySummaryResult(false, month, MonthlySummarySkip.ALREADY_SENT);
        }
        try {
            send(recipient, month);
        } catch (RuntimeException e) {
            sentRepository.release(recipient.userId(), month);
            throw e;
        }
        return new MonthlySummaryResult(true, month, null);
    }

    private boolean summaryEmailEnabled(Long userId) {
        return categoryPreferences.findByUserIdAndCategory(userId, NotificationCategory.SUMMARY)
                .orElseGet(() -> NotificationPreference.defaults(userId, NotificationCategory.SUMMARY))
                .emailEnabled();
    }

    private void send(UserNotificationPreference recipient, YearMonth month) {
        Long userId = recipient.userId();
        List<CategorySummary> categories = financesGateway.getSummaryByCategory(userId,
                month.atDay(1).format(DateTimeFormatter.ISO_LOCAL_DATE),
                month.atEndOfMonth().format(DateTimeFormatter.ISO_LOCAL_DATE));
        String title = "Resumen Mensual - " + month.atDay(1).format(TITLE_MONTH);
        String message = buildMessage(categories);

        Map<String, Object> templateVars = new HashMap<>();
        templateVars.put("title", title);
        templateVars.put("firstName", "Usuario");
        templateVars.put("message", message);
        templateVars.put("categories", categories);
        emailSender.sendTemplatedEmail(recipient.email(), title, "monthly-summary", templateVars);

        notificationService.notify(Notification.create(
                userId, NotificationType.MONTHLY_SUMMARY, title, message, NotificationChannel.BOTH, null));
    }

    private String buildMessage(List<CategorySummary> categories) {
        if (categories.isEmpty()) {
            return "No tuviste transacciones este mes.";
        }
        StringBuilder sb = new StringBuilder("Resumen de tus gastos del mes:\n");
        categories.forEach(cat -> sb.append("- ")
                .append(cat.categoryName())
                .append(": ")
                .append(cat.currency())
                .append(" ")
                .append(cat.totalAmount())
                .append(" (")
                .append(cat.transactionCount())
                .append(" transacciones)\n"));
        return sb.toString();
    }
}
