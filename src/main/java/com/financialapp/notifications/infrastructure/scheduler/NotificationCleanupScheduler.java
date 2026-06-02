package com.financialapp.notifications.infrastructure.scheduler;

import com.financialapp.notifications.domain.usecase.notification.CleanupNotificationsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationCleanupScheduler {

    private final CleanupNotificationsUseCase useCase;

    @Scheduled(cron = "0 0 0 * * *") // Midnight every day
    @Transactional
    public void cleanupOldNotifications() {
        useCase.execute();
    }
}
