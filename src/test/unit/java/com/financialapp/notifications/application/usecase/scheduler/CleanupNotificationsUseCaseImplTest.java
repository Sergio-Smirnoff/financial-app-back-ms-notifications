package com.financialapp.notifications.application.usecase.scheduler;

import com.financialapp.notifications.domain.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CleanupNotificationsUseCaseImplTest {

    @Mock NotificationRepository repository;
    @InjectMocks CleanupNotificationsUseCaseImpl useCase;

    @Test
    void execute_deletesNotificationsOlderThanOneMonth() {
        // Given the repo reports a delete count
        when(repository.deleteOldNotifications(any())).thenReturn(3);

        // When executing the cleanup
        useCase.execute();

        // Then it asks the repo to delete old notifications
        verify(repository).deleteOldNotifications(any());
    }
}
