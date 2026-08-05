package com.financialapp.notifications.application.usecase.event;

import com.financialapp.notifications.application.service.NotificationChannelResolver;
import com.financialapp.notifications.application.usecase.event.impl.ProcessImportStaleUseCaseImpl;
import com.financialapp.notifications.domain.event.ImportStale;
import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import com.financialapp.notifications.domain.service.NotificationService;
import com.financialapp.notifications.domain.usecase.event.command.ProcessImportStaleCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessImportStaleUseCaseImplTest {

    @Mock NotificationService notificationService;
    @Mock NotificationChannelResolver channelResolver;

    @InjectMocks ProcessImportStaleUseCaseImpl useCase;

    @Test
    void dispatchesNotificationWhenChannelResolved() {
        ImportStale event = new ImportStale(42L, "0170099220000067797370", "017", 35);

        when(channelResolver.resolve(42L, NotificationType.IMPORT_STALE))
                .thenReturn(Optional.of(NotificationChannel.IN_APP));

        useCase.execute(new ProcessImportStaleCommand(event));

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationService).notify(captor.capture());

        Notification notif = captor.getValue();
        assertThat(notif.userId()).isEqualTo(42L);
        assertThat(notif.type()).isEqualTo(NotificationType.IMPORT_STALE);
        assertThat(notif.message()).contains("35 days");
    }

    @Test
    void dropsNotificationWhenChannelDisabledByUser() {
        ImportStale event = new ImportStale(42L, "0170099220000067797370", "017", 35);

        when(channelResolver.resolve(42L, NotificationType.IMPORT_STALE))
                .thenReturn(Optional.empty());

        useCase.execute(new ProcessImportStaleCommand(event));

        verify(notificationService, never()).notify(any());
    }
}
