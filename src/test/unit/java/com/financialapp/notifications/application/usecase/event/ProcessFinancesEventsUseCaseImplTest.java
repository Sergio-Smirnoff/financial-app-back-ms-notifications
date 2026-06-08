package com.financialapp.notifications.application.usecase.event;

import com.financialapp.notifications.application.usecase.event.impl.ProcessInstallmentReminderUseCaseImpl;
import com.financialapp.notifications.application.usecase.event.impl.ProcessLoanReminderUseCaseImpl;
import com.financialapp.notifications.application.usecase.event.impl.ProcessPaymentDueUseCaseImpl;
import com.financialapp.notifications.domain.event.InstallmentReminder;
import com.financialapp.notifications.domain.event.LoanReminder;
import com.financialapp.notifications.domain.event.PaymentDue;
import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import com.financialapp.notifications.domain.model.notification.UserNotificationPreference;
import com.financialapp.notifications.domain.service.NotificationService;
import com.financialapp.notifications.domain.usecase.event.command.ProcessInstallmentReminderCommand;
import com.financialapp.notifications.domain.usecase.event.command.ProcessLoanReminderCommand;
import com.financialapp.notifications.domain.usecase.event.command.ProcessPaymentDueCommand;
import com.financialapp.notifications.domain.usecase.preference.GetPreferenceUseCase;
import com.financialapp.notifications.domain.usecase.preference.command.GetPreferenceCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessFinancesEventsUseCaseImplTest {

    @Mock NotificationService notificationService;
    @Mock GetPreferenceUseCase getPreferenceUseCase;

    private void stubPreference(Long userId) {
        when(getPreferenceUseCase.execute(any(GetPreferenceCommand.class)))
                .thenReturn(new UserNotificationPreference(1L, userId, "u@x.com", true, null, null));
    }

    @Test
    void paymentDue_buildsFormattedNotification() {
        stubPreference(1L);
        PaymentDue event = new PaymentDue(1L, "4111111111111234", 5L, 2, 12,
                "TV Purchase", LocalDate.of(2026, 7, 10), new BigDecimal("123.456"), "ARS");

        new ProcessPaymentDueUseCaseImpl(notificationService, getPreferenceUseCase)
                .execute(new ProcessPaymentDueCommand(event));

        Notification n = capture();
        assertThat(n.type()).isEqualTo(NotificationType.PAYMENT_DUE);
        assertThat(n.channel()).isEqualTo(NotificationChannel.BOTH);
        assertThat(n.title()).isEqualTo("Payment Due: TV Purchase");
        assertThat(n.message()).contains("Installment 2/12", "123,46 ARS", "TV Purchase", "2026-07-10");
    }

    @Test
    void loanReminder_buildsFormattedNotification() {
        stubPreference(1L);
        LoanReminder event = new LoanReminder(1L, 5L, 6L, "Car Loan", 3, LocalDate.of(2026, 8, 1));

        new ProcessLoanReminderUseCaseImpl(notificationService, getPreferenceUseCase)
                .execute(new ProcessLoanReminderCommand(event));

        Notification n = capture();
        assertThat(n.type()).isEqualTo(NotificationType.LOAN_REMINDER);
        assertThat(n.title()).isEqualTo("Loan Payment Due: Car Loan");
        assertThat(n.message()).contains("Installment #3", "Car Loan", "2026-08-01");
    }

    @Test
    void installmentReminder_buildsFormattedNotification() {
        stubPreference(1L);
        InstallmentReminder event = new InstallmentReminder(1L, 5L, 6L, "Mortgage",
                3, LocalDate.of(2026, 9, 15), new BigDecimal("250"), "ARS");

        new ProcessInstallmentReminderUseCaseImpl(notificationService, getPreferenceUseCase)
                .execute(new ProcessInstallmentReminderCommand(event));

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
