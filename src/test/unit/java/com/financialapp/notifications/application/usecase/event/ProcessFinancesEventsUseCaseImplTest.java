package com.financialapp.notifications.application.usecase.event;

import com.financialapp.notifications.domain.service.NotificationService;
import com.financialapp.notifications.application.usecase.event.impl.ProcessInstallmentReminderUseCaseImpl;
import com.financialapp.notifications.application.usecase.event.impl.ProcessLoanReminderUseCaseImpl;
import com.financialapp.notifications.application.usecase.event.impl.ProcessPaymentDueUseCaseImpl;
import com.financialapp.notifications.domain.event.InstallmentReminder;
import com.financialapp.notifications.domain.event.LoanReminder;
import com.financialapp.notifications.domain.event.PaymentDue;
import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import com.financialapp.notifications.domain.usecase.event.command.ProcessInstallmentReminderCommand;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProcessFinancesEventsUseCaseImplTest {

        @Mock
        NotificationService notificationService;

        @Test
        void paymentDue_buildsFormattedNotification() {
                // Given a payment-due event
                PaymentDue event = new PaymentDue(1L, 5L, "Visa",
                                LocalDate.of(2026, 7, 10), new BigDecimal("123.456"), "ARS", 2);

                // When executed
                new ProcessPaymentDueUseCaseImpl(notificationService).execute(new ProcessPaymentDueCommand(event));

                // Then a PAYMENT_DUE notification with a formatted message is dispatched
                Notification n = capture();
                assertThat(n.type()).isEqualTo(NotificationType.PAYMENT_DUE);
                assertThat(n.channel()).isEqualTo(NotificationChannel.BOTH);
                assertThat(n.title()).isEqualTo("Payment Due: Visa");
                assertThat(n.message()).isEqualTo(
                                "Your payment of 123,46 ARS for 'Visa' is due on 2026-07-10. 2 installment(s) remaining.");
        }

        @Test
        void loanReminder_buildsFormattedNotification() {
                // Given a loan-reminder event
                LoanReminder event = new LoanReminder(1L, 5L, "Car loan",
                                LocalDate.of(2026, 8, 1), new BigDecimal("99.9"), "USD", 4);

                // When executed
                new ProcessLoanReminderUseCaseImpl(notificationService).execute(new ProcessLoanReminderCommand(event));

                // Then a LOAN_REMINDER notification with a formatted message is dispatched
                Notification n = capture();
                assertThat(n.type()).isEqualTo(NotificationType.LOAN_REMINDER);
                assertThat(n.title()).isEqualTo("Loan Payment Due: Car loan");
                assertThat(n.message()).isEqualTo(
                                "Your loan payment of 99,90 USD for 'Car loan' is due on 2026-08-01. 4 installment(s) remaining.");
        }

        @Test
        void installmentReminder_buildsFormattedNotification() {
                // Given an installment-reminder event
                InstallmentReminder event = new InstallmentReminder(1L, 5L, 6L, "Mortgage",
                                3, LocalDate.of(2026, 9, 15), new BigDecimal("250"), "ARS");

                // When executed
                new ProcessInstallmentReminderUseCaseImpl(notificationService)
                                .execute(new ProcessInstallmentReminderCommand(event));

                // Then an INSTALLMENT_REMINDER notification with a formatted message is
                // dispatched
                Notification n = capture();
                assertThat(n.type()).isEqualTo(NotificationType.INSTALLMENT_REMINDER);
                assertThat(n.title()).isEqualTo("Installment #3 Due: Mortgage");
                assertThat(n.message()).isEqualTo(
                                "Installment #3 of 250,00 ARS for loan 'Mortgage' is due on 2026-09-15.");
        }

        private Notification capture() {
                ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
                verify(notificationService).notify(captor.capture());
                return captor.getValue();
        }
}
