package com.financialapp.notifications.application.usecase.event;

import com.financialapp.notifications.application.service.NotificationService;
import com.financialapp.notifications.domain.model.entity.Notification;
import com.financialapp.notifications.domain.model.entity.enums.NotificationChannel;
import com.financialapp.notifications.domain.model.entity.enums.NotificationType;
import com.financialapp.notifications.domain.model.entity.event.InstallmentReminder;
import com.financialapp.notifications.domain.model.entity.event.LoanReminder;
import com.financialapp.notifications.domain.model.entity.event.PaymentDue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProcessFinancesEventsUseCaseImplTest {

    @Mock NotificationService notificationService;

    @Test
    void paymentDue_buildsFormattedNotification() {
        // Given a payment-due event
        PaymentDue event = PaymentDue.builder().userId(1L).cardExpenseId(5L).description("Visa")
                .nextDueDate(LocalDate.of(2026, 7, 10)).installmentAmount(new BigDecimal("123.456"))
                .currency("ARS").remainingInstallments(2).build();

        // When executed
        new ProcessPaymentDueUseCaseImpl(notificationService).execute(event);

        // Then a PAYMENT_DUE notification with a formatted message is dispatched
        Notification n = capture();
        assertThat(n.type()).isEqualTo(NotificationType.PAYMENT_DUE);
        assertThat(n.channel()).isEqualTo(NotificationChannel.BOTH);
        assertThat(n.title()).isEqualTo("Payment Due: Visa");
        assertThat(n.message()).isEqualTo(
                "Your payment of 123.46 ARS for 'Visa' is due on 2026-07-10. 2 installment(s) remaining.");
    }

    @Test
    void loanReminder_buildsFormattedNotification() {
        // Given a loan-reminder event
        LoanReminder event = LoanReminder.builder().userId(1L).loanId(5L).loanDescription("Car loan")
                .nextPaymentDate(LocalDate.of(2026, 8, 1)).installmentAmount(new BigDecimal("99.9"))
                .currency("USD").remainingInstallments(4).build();

        // When executed
        new ProcessLoanReminderUseCaseImpl(notificationService).execute(event);

        // Then a LOAN_REMINDER notification with a formatted message is dispatched
        Notification n = capture();
        assertThat(n.type()).isEqualTo(NotificationType.LOAN_REMINDER);
        assertThat(n.title()).isEqualTo("Loan Payment Due: Car loan");
        assertThat(n.message()).isEqualTo(
                "Your loan payment of 99.90 USD for 'Car loan' is due on 2026-08-01. 4 installment(s) remaining.");
    }

    @Test
    void installmentReminder_buildsFormattedNotification() {
        // Given an installment-reminder event
        InstallmentReminder event = InstallmentReminder.builder().userId(1L).loanId(5L).installmentId(6L)
                .loanDescription("Mortgage").installmentNumber(3).dueDate(LocalDate.of(2026, 9, 15))
                .amount(new BigDecimal("250")).currency("ARS").build();

        // When executed
        new ProcessInstallmentReminderUseCaseImpl(notificationService).execute(event);

        // Then an INSTALLMENT_REMINDER notification with a formatted message is dispatched
        Notification n = capture();
        assertThat(n.type()).isEqualTo(NotificationType.INSTALLMENT_REMINDER);
        assertThat(n.title()).isEqualTo("Installment #3 Due: Mortgage");
        assertThat(n.message()).isEqualTo(
                "Installment #3 of 250.00 ARS for loan 'Mortgage' is due on 2026-09-15.");
    }

    private Notification capture() {
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationService).notify(captor.capture());
        return captor.getValue();
    }
}
