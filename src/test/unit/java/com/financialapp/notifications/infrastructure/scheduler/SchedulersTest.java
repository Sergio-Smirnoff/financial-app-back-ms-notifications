package com.financialapp.notifications.infrastructure.scheduler;

import com.financialapp.notifications.domain.usecase.notification.CleanupNotificationsUseCase;
import com.financialapp.notifications.domain.usecase.notification.SendMonthlySummariesUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SchedulersTest {

    @Mock SendMonthlySummariesUseCase monthlyUseCase;
    @Mock CleanupNotificationsUseCase cleanupUseCase;

    @Test
    void monthlySummaryScheduler_delegatesToUseCase() {
        // Given the monthly scheduler / When the cron fires
        new MonthlySummaryScheduler(monthlyUseCase).sendMonthlySummaries();

        // Then it delegates to the use case
        verify(monthlyUseCase).execute();
    }

    @Test
    void cleanupScheduler_delegatesToUseCase() {
        // Given the cleanup scheduler / When the cron fires
        new NotificationCleanupScheduler(cleanupUseCase).cleanupOldNotifications();

        // Then it delegates to the use case
        verify(cleanupUseCase).execute();
    }
}
