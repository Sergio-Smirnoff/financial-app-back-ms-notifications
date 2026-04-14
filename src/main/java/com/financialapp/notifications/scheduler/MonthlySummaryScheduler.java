package com.financialapp.notifications.scheduler;

import com.financialapp.notifications.client.CategorySummaryResponse;
import com.financialapp.notifications.client.FinancesClient;
import com.financialapp.notifications.model.entity.UserNotificationPreference;
import com.financialapp.notifications.model.enums.NotificationChannel;
import com.financialapp.notifications.model.enums.NotificationType;
import com.financialapp.notifications.repository.UserNotificationPreferenceRepository;
import com.financialapp.notifications.service.EmailService;
import com.financialapp.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class MonthlySummaryScheduler {

    private final UserNotificationPreferenceRepository preferenceRepository;
    private final FinancesClient financesClient;
    private final NotificationService notificationService;
    private final EmailService emailService;

    @Scheduled(cron = "${notification.scheduler.cron:0 0 9 1 * *}")
    public void sendMonthlySummaries() {
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
        List<CategorySummaryResponse> categories = financesClient.getSummaryByCategory(userId, dateFrom, dateTo);

        String title = "Resumen Mensual - " + LocalDate.now().minusMonths(1).format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        String message = buildMessage(categories);

        notificationService.createAndDispatch(
                userId,
                NotificationType.MONTHLY_SUMMARY,
                title,
                message,
                NotificationChannel.BOTH,
                null
        );

        Map<String, Object> templateVars = new HashMap<>();
        templateVars.put("title", title);
        templateVars.put("firstName", "Usuario");
        templateVars.put("message", message);
        templateVars.put("categories", categories);
        emailService.sendTemplatedEmail(pref.getEmail(), title, "monthly-summary", templateVars);

        log.debug("Sent monthly summary to userId={}", userId);
    }

    private String buildMessage(List<CategorySummaryResponse> categories) {
        if (categories.isEmpty()) {
            return "No tuviste transacciones este mes.";
        }
        StringBuilder sb = new StringBuilder("Resumen de tus gastos del mes:\n");
        categories.forEach(cat -> {
            sb.append("- ")
              .append(cat.getCategoryName())
              .append(": ")
              .append(cat.getCurrency())
              .append(" ")
              .append(cat.getTotalAmount())
              .append(" (")
              .append(cat.getTransactionCount())
              .append(" transacciones)\n");
        });
        return sb.toString();
    }
}
