package com.financialapp.notifications.application.usecase.event;

import com.financialapp.notifications.application.service.NotificationChannelResolver;
import com.financialapp.notifications.application.usecase.event.impl.ProcessInvestmentThresholdUseCaseImpl;
import com.financialapp.notifications.domain.event.InvestmentThreshold;
import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import com.financialapp.notifications.domain.service.NotificationService;
import com.financialapp.notifications.domain.usecase.event.command.ProcessInvestmentThresholdCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessInvestmentThresholdUseCaseImplTest {

    @Mock NotificationService notificationService;
    @Mock NotificationChannelResolver channelResolver;
    @InjectMocks ProcessInvestmentThresholdUseCaseImpl useCase;

    @Test
    void execute_gainDirection_usesGainWording() {
        when(channelResolver.resolve(anyLong())).thenReturn(NotificationChannel.BOTH);
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
        when(channelResolver.resolve(anyLong())).thenReturn(NotificationChannel.BOTH);
        InvestmentThreshold event = new InvestmentThreshold(1L, 2L, "GD30", "Bond", "LOSS",
                new BigDecimal("5"), new BigDecimal("-8"),
                new BigDecimal("90"), new BigDecimal("100"), "USD");

        useCase.execute(new ProcessInvestmentThresholdCommand(event));

        Notification n = capture();
        assertThat(n.title()).isEqualTo("Investment Alert: GD30 lost 8,00%");
        assertThat(n.message()).contains("has lost 8,00%", "crossing your loss threshold");
    }

    @Test
    void execute_emailDisabled_usesInAppChannel() {
        when(channelResolver.resolve(anyLong())).thenReturn(NotificationChannel.IN_APP);
        InvestmentThreshold event = new InvestmentThreshold(1L, 2L, "AL30", "Bond", "GAIN",
                new BigDecimal("5"), new BigDecimal("7"),
                new BigDecimal("110"), new BigDecimal("100"), "USD");

        useCase.execute(new ProcessInvestmentThresholdCommand(event));

        Notification n = capture();
        assertThat(n.channel()).isEqualTo(NotificationChannel.IN_APP);
        assertThat(n.type()).isEqualTo(NotificationType.INVESTMENT_THRESHOLD);
        assertThat(n.title()).contains("AL30 gained");
    }

    private Notification capture() {
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationService).notify(captor.capture());
        return captor.getValue();
    }
}
