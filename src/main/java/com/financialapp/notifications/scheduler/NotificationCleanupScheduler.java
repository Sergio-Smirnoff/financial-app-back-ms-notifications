package com.financialapp.notifications.scheduler;

import com.financialapp.notifications.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationCleanupScheduler {

    private final NotificationRepository notificationRepository;

    @Scheduled(cron = "0 0 0 * * *") // Midnight every day
    @Transactional
    public void cleanupOldNotifications() {
        log.info("Starting nightly notification cleanup...");
        LocalDateTime threshold = LocalDateTime.now().minusMonths(1);
        int deletedCount = notificationRepository.deleteOldNotifications(threshold);
        log.info("Deleted {} notifications older than 1 month.", deletedCount);
    }
}
