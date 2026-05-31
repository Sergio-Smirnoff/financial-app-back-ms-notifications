package com.financialapp.notifications.application.usecase.scheduler;

import com.financialapp.notifications.application.service.NotificationService;
import com.financialapp.notifications.domain.gateway.FinancesGateway;
import com.financialapp.notifications.domain.messaging.EmailSender;
import com.financialapp.notifications.domain.model.entity.Notification;
import com.financialapp.notifications.domain.model.entity.summary.CategorySummary;
import com.financialapp.notifications.domain.repository.UserNotificationPreferenceRepository;
import com.financialapp.notifications.domain.interfaces.usecase.SendMonthlySummariesUseCase;
import com.financialapp.notifications.domain.model.entity.UserNotificationPreference;
import com.financialapp.notifications.domain.model.entity.enums.NotificationChannel;
import com.financialapp.notifications.domain.model.entity.enums.NotificationType;
import com.financialapp.notifications.infrastructure.client.dto.CategorySummaryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendMonthlySummariesUseCaseImpl implements SendMonthlySummariesUseCase {

    private final UserNotificationPreferenceRepository preferenceRepository;
    private final FinancesGateway financesGateway;
    private final NotificationService notificationService;
    private final EmailSender emailSender;

    public void execute() {
        log.info("Starting monthly summary job");

        LocalDate today = LocalDate.now();
        LocalDate firstOfMonth = today.withDayOfMonth(1);
        String dateFrom = firstOfMonth.minusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        String dateTo = firstOfMonth.minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);

        Pageable pageable = PageRequest.of(0, 500);
        Page<UserNotificationPreference> page;
        int totalProcessed = 0;
        do {
            page = preferenceRepository.findByMonthlyEmailEnabledTrue(pageable);
            processPage(page.getContent(), dateFrom, dateTo);
            totalProcessed += page.getNumberOfElements();
            pageable = pageable.next();
        } while (page.hasNext());
        log.info("Monthly summary job completed, processed {} users", totalProcessed);
    }

    private void processPage(List<UserNotificationPreference> prefs, String dateFrom, String dateTo) {
        List<CompletableFuture<Void>> futures = prefs.stream()
                .map(pref -> CompletableFuture.runAsync(() -> processSingleUser(pref, dateFrom, dateTo)))
                .toList();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private void processSingleUser(UserNotificationPreference pref, String dateFrom, String dateTo) {
        try {
            processUser(pref, dateFrom, dateTo);
        } catch (Exception e) {
            log.error("Failed to process monthly summary for userId={}: {}", pref.getUserId(), e.getMessage());
        }
    }

    private void processUser(UserNotificationPreference pref, String dateFrom, String dateTo) {
        Long userId = pref.getUserId();
        List<CategorySummary> categories = financesGateway.getSummaryByCategory(userId, dateFrom, dateTo);

        String title = "Resumen Mensual - " + LocalDate.now().minusMonths(1).format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        String message = buildMessage(categories);

        var newNotification = Notification.create(
                userId,
                NotificationType.MONTHLY_SUMMARY,
                title,
                message,
                NotificationChannel.BOTH,
                null
        );
        notificationService.notify(newNotification);

        Map<String, Object> templateVars = new HashMap<>();
        templateVars.put("title", title);
        templateVars.put("firstName", "Usuario");
        templateVars.put("message", message);
        templateVars.put("categories", categories);
        emailSender.sendTemplatedEmail(pref.getEmail(), title, "monthly-summary", templateVars);

        log.debug("Sent monthly summary to userId={}", userId);
    }

    private String buildMessage(List<CategorySummary> categories) {
        if (categories.isEmpty()) {
            return "No tuviste transacciones este mes.";
        }
        StringBuilder sb = new StringBuilder("Resumen de tus gastos del mes:\n");
        categories.forEach(cat -> {
            sb.append("- ")
                    .append(cat.categoryName())
                    .append(": ")
                    .append(cat.currency())
                    .append(" ")
                    .append(cat.totalAmount())
                    .append(" (")
                    .append(cat.transactionCount())
                    .append(" transacciones)\n");
        });
        return sb.toString();
    }
}