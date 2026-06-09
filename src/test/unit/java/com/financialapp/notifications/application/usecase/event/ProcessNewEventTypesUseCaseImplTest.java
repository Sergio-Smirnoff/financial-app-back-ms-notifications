package com.financialapp.notifications.application.usecase.event;

import com.financialapp.notifications.application.service.NotificationChannelResolver;
import com.financialapp.notifications.application.usecase.event.impl.ProcessBalanceAdjustedUseCaseImpl;
import com.financialapp.notifications.application.usecase.event.impl.ProcessCardExpiringUseCaseImpl;
import com.financialapp.notifications.application.usecase.event.impl.ProcessLowBalanceUseCaseImpl;
import com.financialapp.notifications.domain.event.BalanceAdjusted;
import com.financialapp.notifications.domain.event.CardExpiring;
import com.financialapp.notifications.domain.event.LowBalance;
import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import com.financialapp.notifications.domain.service.NotificationService;
import com.financialapp.notifications.domain.usecase.event.command.ProcessBalanceAdjustedCommand;
import com.financialapp.notifications.domain.usecase.event.command.ProcessCardExpiringCommand;
import com.financialapp.notifications.domain.usecase.event.command.ProcessLowBalanceCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessNewEventTypesUseCaseImplTest {

    @Mock NotificationService notificationService;
    @Mock NotificationChannelResolver channelResolver;

    private void stubEmailEnabled() {
        when(channelResolver.resolve(anyLong())).thenReturn(NotificationChannel.BOTH);
    }

    private void stubEmailDisabled() {
        when(channelResolver.resolve(anyLong())).thenReturn(NotificationChannel.IN_APP);
    }

    private Notification captureNotification() {
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationService).notify(captor.capture());
        return captor.getValue();
    }

    @Test
    void processLowBalance_withEmailEnabled_usesBothChannel() {
        stubEmailEnabled();
        LowBalance event = new LowBalance(1L, "Savings", "001", "BANCO", new BigDecimal("50"), "ARS");

        new ProcessLowBalanceUseCaseImpl(notificationService, channelResolver)
                .execute(new ProcessLowBalanceCommand(event));

        Notification n = captureNotification();
        assertThat(n.type()).isEqualTo(NotificationType.LOW_BALANCE);
        assertThat(n.channel()).isEqualTo(NotificationChannel.BOTH);
        assertThat(n.title()).contains("Savings");
        assertThat(n.message()).contains("50,00 ARS");
    }

    @Test
    void processLowBalance_withEmailDisabled_usesInAppChannel() {
        stubEmailDisabled();
        LowBalance event = new LowBalance(1L, "Savings", "001", "BANCO", new BigDecimal("50"), "ARS");

        new ProcessLowBalanceUseCaseImpl(notificationService, channelResolver)
                .execute(new ProcessLowBalanceCommand(event));

        Notification n = captureNotification();
        assertThat(n.channel()).isEqualTo(NotificationChannel.IN_APP);
        assertThat(n.type()).isEqualTo(NotificationType.LOW_BALANCE);
        assertThat(n.title()).contains("Savings");
    }

    @Test
    void processBalanceAdjusted_credit_buildsCorrectMessage() {
        stubEmailEnabled();
        BalanceAdjusted event = new BalanceAdjusted(1L, "Checking", "002", "BANCO",
                new BigDecimal("200"), "ARS", true);

        new ProcessBalanceAdjustedUseCaseImpl(notificationService, channelResolver)
                .execute(new ProcessBalanceAdjustedCommand(event));

        Notification n = captureNotification();
        assertThat(n.type()).isEqualTo(NotificationType.BALANCE_ADJUSTED);
        assertThat(n.title()).contains("Credited");
        assertThat(n.message()).contains("credited", "200,00 ARS");
    }

    @Test
    void processBalanceAdjusted_debit_buildsCorrectMessage() {
        stubEmailEnabled();
        BalanceAdjusted event = new BalanceAdjusted(1L, "Checking", "002", "BANCO",
                new BigDecimal("100"), "USD", false);

        new ProcessBalanceAdjustedUseCaseImpl(notificationService, channelResolver)
                .execute(new ProcessBalanceAdjustedCommand(event));

        Notification n = captureNotification();
        assertThat(n.title()).contains("Debited");
        assertThat(n.message()).contains("debited");
    }

    @Test
    void processCardExpiring_masksCardNumber_andBuildsMessage() {
        stubEmailEnabled();
        CardExpiring event = new CardExpiring(1L, "4111111111111234", "BANK01", "2027-12");

        new ProcessCardExpiringUseCaseImpl(notificationService, channelResolver)
                .execute(new ProcessCardExpiringCommand(event));

        Notification n = captureNotification();
        assertThat(n.type()).isEqualTo(NotificationType.CARD_EXPIRING);
        assertThat(n.title()).contains("****1234");
        assertThat(n.message()).contains("2027-12");
    }

    @Test
    void processCardExpiring_shortCardNumber_isLeftUnmasked() {
        stubEmailDisabled();
        CardExpiring event = new CardExpiring(1L, "12", "BANK01", "2027-12");

        new ProcessCardExpiringUseCaseImpl(notificationService, channelResolver)
                .execute(new ProcessCardExpiringCommand(event));

        Notification n = captureNotification();
        assertThat(n.title()).contains("12");
        assertThat(n.title()).doesNotContain("****");
    }

    @Test
    void processCardExpiring_nullCardNumber_isLeftUnmasked() {
        stubEmailDisabled();
        CardExpiring event = new CardExpiring(1L, null, "BANK01", "2027-12");

        new ProcessCardExpiringUseCaseImpl(notificationService, channelResolver)
                .execute(new ProcessCardExpiringCommand(event));

        Notification n = captureNotification();
        assertThat(n.type()).isEqualTo(NotificationType.CARD_EXPIRING);
        assertThat(n.title()).doesNotContain("****");
    }
}
