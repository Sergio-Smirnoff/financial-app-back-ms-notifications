package com.financialapp.notifications.application.usecase.event;

import com.financialapp.notifications.application.service.NotificationService;
import com.financialapp.notifications.application.usecase.event.impl.ProcessBankEventUseCaseImpl;
import com.financialapp.notifications.domain.event.BankAlert;
import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import com.financialapp.notifications.domain.usecase.event.command.ProcessBankEventCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ProcessBankEventUseCaseImplTest {

    @Mock NotificationService notificationService;
    @InjectMocks ProcessBankEventUseCaseImpl useCase;

    @Test
    void execute_knownType_buildsNotificationAndNotifies() {
        // Given a bank alert with a known type
        BankAlert alert = new BankAlert(3L, "CARD_EXPIRING", "Card", "expiring", "{\"bankId\":\"7\"}");

        // When executed
        useCase.execute(new ProcessBankEventCommand(alert));

        // Then a BOTH-channel CARD_EXPIRING notification is dispatched
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationService).notify(captor.capture());
        Notification n = captor.getValue();
        assertThat(n.userId()).isEqualTo(3L);
        assertThat(n.type()).isEqualTo(NotificationType.CARD_EXPIRING);
        assertThat(n.title()).isEqualTo("Card");
        assertThat(n.message()).isEqualTo("expiring");
        assertThat(n.channel()).isEqualTo(NotificationChannel.BOTH);
        assertThat(n.metadata()).isEqualTo("{\"bankId\":\"7\"}");
    }

    @Test
    void execute_unknownType_isIgnored() {
        // Given a bank alert with an unrecognised type
        BankAlert alert = new BankAlert(3L, "NOT_A_TYPE", null, null, null);

        // When executed / Then no notification is dispatched
        useCase.execute(new ProcessBankEventCommand(alert));
        verifyNoInteractions(notificationService);
    }
}
