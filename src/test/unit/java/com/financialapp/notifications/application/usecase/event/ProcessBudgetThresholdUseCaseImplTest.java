package com.financialapp.notifications.application.usecase.event;

import com.financialapp.notifications.application.service.NotificationChannelResolver;
import com.financialapp.notifications.application.usecase.event.impl.ProcessBudgetThresholdUseCaseImpl;
import com.financialapp.notifications.domain.event.BudgetThresholdReached;
import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import com.financialapp.notifications.domain.service.NotificationService;
import com.financialapp.notifications.domain.usecase.event.command.ProcessBudgetThresholdCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessBudgetThresholdUseCaseImplTest {

    @Mock NotificationService notificationService;
    @Mock NotificationChannelResolver channelResolver;

    @InjectMocks ProcessBudgetThresholdUseCaseImpl useCase;

    @Test
    void dispatchesNotificationWhenChannelResolved() {
        BudgetThresholdReached event = new BudgetThresholdReached(
                100L, 42L, 5L, new BigDecimal("85.5"), new BigDecimal("80.0"), 2026, 8);

        when(channelResolver.resolve(42L, NotificationType.BUDGET_THRESHOLD_REACHED))
                .thenReturn(Optional.of(NotificationChannel.IN_APP));

        useCase.execute(new ProcessBudgetThresholdCommand(event));

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationService).notify(captor.capture());

        Notification notif = captor.getValue();
        assertThat(notif.userId()).isEqualTo(42L);
        assertThat(notif.type()).isEqualTo(NotificationType.BUDGET_THRESHOLD_REACHED);
        assertThat(notif.title()).contains("85");
    }

    @Test
    void dropsNotificationWhenChannelDisabledByUser() {
        BudgetThresholdReached event = new BudgetThresholdReached(
                100L, 42L, 5L, new BigDecimal("85.5"), new BigDecimal("80.0"), 2026, 8);

        when(channelResolver.resolve(42L, NotificationType.BUDGET_THRESHOLD_REACHED))
                .thenReturn(Optional.empty());

        useCase.execute(new ProcessBudgetThresholdCommand(event));

        verify(notificationService, never()).notify(any());
    }
}
