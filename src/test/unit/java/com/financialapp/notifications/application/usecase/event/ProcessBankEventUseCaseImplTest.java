package com.financialapp.notifications.application.usecase.event;

import com.financialapp.notifications.application.usecase.event.impl.ProcessBankEventUseCaseImpl;
import com.financialapp.notifications.domain.event.BankAlert;
import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import com.financialapp.notifications.domain.model.notification.UserNotificationPreference;
import com.financialapp.notifications.domain.service.NotificationService;
import com.financialapp.notifications.domain.usecase.event.command.ProcessBankEventCommand;
import com.financialapp.notifications.domain.usecase.preference.GetPreferenceUseCase;
import com.financialapp.notifications.domain.usecase.preference.command.GetPreferenceCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessBankEventUseCaseImplTest {

    @Mock NotificationService notificationService;
    @Mock GetPreferenceUseCase getPreferenceUseCase;
    @InjectMocks ProcessBankEventUseCaseImpl useCase;

    @Test
    void execute_knownType_buildsNotificationAndNotifies() {
        when(getPreferenceUseCase.execute(any(GetPreferenceCommand.class)))
                .thenReturn(new UserNotificationPreference(1L, 3L, "u@x.com", true, null, null));
        BankAlert alert = new BankAlert(3L, "CARD_EXPIRING", "Card", "expiring", "{\"bankId\":\"7\"}");

        useCase.execute(new ProcessBankEventCommand(alert));

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
        BankAlert alert = new BankAlert(3L, "NOT_A_TYPE", null, null, null);

        useCase.execute(new ProcessBankEventCommand(alert));

        verifyNoInteractions(notificationService);
    }
}
