package com.financialapp.notifications.application.usecase.event;

import com.financialapp.notifications.application.service.NotificationChannelResolver;
import com.financialapp.notifications.application.usecase.event.impl.ProcessLoanReminderUseCaseImpl;
import com.financialapp.notifications.application.usecase.event.impl.ProcessPaymentDueUseCaseImpl;
import com.financialapp.notifications.domain.event.LoanReminder;
import com.financialapp.notifications.domain.event.PaymentDue;
import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import com.financialapp.notifications.domain.service.NotificationService;
import com.financialapp.notifications.domain.usecase.event.command.ProcessLoanReminderCommand;
import com.financialapp.notifications.domain.usecase.event.command.ProcessPaymentDueCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessFinancesEventsUseCaseImplTest {

    @Mock NotificationService notificationService;
    @Mock NotificationChannelResolver channelResolver;

    private void stubEmailEnabled() {
        when(channelResolver.resolve(anyLong())).thenReturn(NotificationChannel.BOTH);
    }

    @Test
    void paymentDue_buildsFormattedNotification() {
        stubEmailEnabled();
        PaymentDue event = new PaymentDue(1L, "4111111111111234", 5L, 2, 12,
                "TV Purchase", LocalDate.of(2026, 7, 10), new BigDecimal("123.456"), "ARS");

        new ProcessPaymentDueUseCaseImpl(notificationService, channelResolver)
                .execute(new ProcessPaymentDueCommand(event));

        Notification n = capture();
        assertThat(n.type()).isEqualTo(NotificationType.PAYMENT_DUE);
        assertThat(n.channel()).isEqualTo(NotificationChannel.BOTH);
        assertThat(n.title()).isEqualTo("Payment Due: TV Purchase");
        assertThat(n.message()).contains("Installment 2/12", "123,46 ARS", "TV Purchase", "2026-07-10");
    }

    @Test
    void loanReminder_buildsFormattedNotification() {
        stubEmailEnabled();
        LoanReminder event = new LoanReminder(1L, 5L, 6L, "Car Loan", 3, LocalDate.of(2026, 8, 1));

        new ProcessLoanReminderUseCaseImpl(notificationService, channelResolver)
                .execute(new ProcessLoanReminderCommand(event));

        Notification n = capture();
        assertThat(n.type()).isEqualTo(NotificationType.LOAN_REMINDER);
        assertThat(n.title()).isEqualTo("Loan Payment Due: Car Loan");
        assertThat(n.message()).contains("Installment #3", "Car Loan", "2026-08-01");
    }

    private Notification capture() {
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationService).notify(captor.capture());
        return captor.getValue();
    }
}
