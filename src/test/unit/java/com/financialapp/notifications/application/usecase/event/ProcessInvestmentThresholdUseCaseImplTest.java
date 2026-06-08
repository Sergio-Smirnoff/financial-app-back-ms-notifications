package com.financialapp.notifications.application.usecase.event;

import com.financialapp.notifications.application.usecase.event.impl.ProcessInvestmentThresholdUseCaseImpl;
import com.financialapp.notifications.domain.event.InvestmentThreshold;
import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import com.financialapp.notifications.domain.model.notification.UserNotificationPreference;
import com.financialapp.notifications.domain.service.NotificationService;
import com.financialapp.notifications.domain.usecase.event.command.ProcessInvestmentThresholdCommand;
import com.financialapp.notifications.domain.usecase.preference.GetPreferenceUseCase;
import com.financialapp.notifications.domain.usecase.preference.command.GetPreferenceCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessInvestmentThresholdUseCaseImplTest {

    @Mock NotificationService notificationService;
    @Mock GetPreferenceUseCase getPreferenceUseCase;
    @InjectMocks ProcessInvestmentThresholdUseCaseImpl useCase;

    @Test
    void execute_gainDirection_usesGainWording() {
        when(getPreferenceUseCase.execute(any(GetPreferenceCommand.class)))
                .thenReturn(new UserNotificationPreference(1L, 1L, "a@b.com", true, null, null));
        InvestmentThreshold event = new InvestmentThreshold(1L, 2L, "AL30", "Bond", "GAIN",
                new BigDecimal("5"), new BigDecimal("-7.5"),
                new BigDecimal("110"), new BigDecimal("100"), "USD");

        useCase.execute(new ProcessInvestmentThresholdCommand(event));

        Notification n = capture();
        assertThat(n.type()).isEqualTo(NotificationType.INVESTMENT_THRESHOLD);
        assertThat(n.title()).isEqualTo("Investment Alert: AL30 gained 7,50%");
        assertThat(n.message()).contains("has gained 7,50%", "crossing your gain threshold");
        assertThat(n.channel()).isEqualTo(NotificationChannel.BOTH);
    }

    @Test
    void execute_lossDirection_usesLossWording() {
        when(getPreferenceUseCase.execute(any(GetPreferenceCommand.class)))
                .thenReturn(new UserNotificationPreference(1L, 1L, "a@b.com", true, null, null));
        InvestmentThreshold event = new InvestmentThreshold(1L, 2L, "GD30", "Bond", "LOSS",
                new BigDecimal("5"), new BigDecimal("-8"),
                new BigDecimal("90"), new BigDecimal("100"), "USD");

        useCase.execute(new ProcessInvestmentThresholdCommand(event));

        Notification n = capture();
        assertThat(n.title()).isEqualTo("Investment Alert: GD30 lost 8,00%");
        assertThat(n.message()).contains("has lost 8,00%", "crossing your loss threshold");
    }

    @Test
    void execute_noPreference_usesInAppChannel() {
        when(getPreferenceUseCase.execute(any(GetPreferenceCommand.class)))
                .thenThrow(new RuntimeException("not found"));
        InvestmentThreshold event = new InvestmentThreshold(1L, 2L, "AL30", "Bond", "GAIN",
                new BigDecimal("5"), new BigDecimal("7"),
                new BigDecimal("110"), new BigDecimal("100"), "USD");

        useCase.execute(new ProcessInvestmentThresholdCommand(event));

        Notification n = capture();
        assertThat(n.channel()).isEqualTo(NotificationChannel.IN_APP);
    }

    private Notification capture() {
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationService).notify(captor.capture());
        return captor.getValue();
    }
}
