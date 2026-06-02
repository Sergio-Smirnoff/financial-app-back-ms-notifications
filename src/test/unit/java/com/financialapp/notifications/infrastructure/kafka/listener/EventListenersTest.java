package com.financialapp.notifications.infrastructure.kafka.listener;

import com.financialapp.notifications.domain.model.entity.event.BankAlert;
import com.financialapp.notifications.domain.model.entity.event.InstallmentReminder;
import com.financialapp.notifications.domain.model.entity.event.InvestmentThreshold;
import com.financialapp.notifications.domain.model.entity.event.LoanReminder;
import com.financialapp.notifications.domain.model.entity.event.PaymentDue;
import com.financialapp.notifications.domain.model.entity.event.UserRegistered;
import com.financialapp.notifications.domain.usecase.event.ProcessBankEventUseCase;
import com.financialapp.notifications.domain.usecase.event.ProcessInstallmentReminderUseCase;
import com.financialapp.notifications.domain.usecase.event.ProcessInvestmentThresholdUseCase;
import com.financialapp.notifications.domain.usecase.event.ProcessLoanReminderUseCase;
import com.financialapp.notifications.domain.usecase.event.ProcessPaymentDueUseCase;
import com.financialapp.notifications.domain.usecase.event.ProcessUserRegisteredUseCase;
import com.financialapp.notifications.infrastructure.kafka.event.BankAlertEvent;
import com.financialapp.notifications.infrastructure.kafka.event.InstallmentReminderEvent;
import com.financialapp.notifications.infrastructure.kafka.event.InvestmentThresholdEvent;
import com.financialapp.notifications.infrastructure.kafka.event.LoanReminderEvent;
import com.financialapp.notifications.infrastructure.kafka.event.PaymentDueEvent;
import com.financialapp.notifications.infrastructure.kafka.event.UserRegisteredEvent;
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
class EventListenersTest {

    @Mock ProcessBankEventUseCase bankUseCase;
    @Mock ProcessPaymentDueUseCase paymentDueUseCase;
    @Mock ProcessLoanReminderUseCase loanReminderUseCase;
    @Mock ProcessInstallmentReminderUseCase installmentReminderUseCase;
    @Mock ProcessInvestmentThresholdUseCase investmentUseCase;
    @Mock ProcessUserRegisteredUseCase userUseCase;

    @Test
    void bankEventListener_mapsAndDelegates() {
        // Given a bank-alert event / When the listener handles it
        new BankEventListener(bankUseCase).handleBankAlert(BankAlertEvent.builder()
                .userId(1L).type("LOW_BALANCE").title("t").message("m").build());

        // Then it delegates the mapped domain alert
        ArgumentCaptor<BankAlert> captor = ArgumentCaptor.forClass(BankAlert.class);
        verify(bankUseCase).execute(captor.capture());
        assertThat(captor.getValue().userId()).isEqualTo(1L);
        assertThat(captor.getValue().type()).isEqualTo("LOW_BALANCE");
    }

    @Test
    void financesEventListener_handlesPaymentDue() {
        // Given a payment-due event / When the listener handles it
        FinancesEventListener listener = new FinancesEventListener(paymentDueUseCase, loanReminderUseCase, installmentReminderUseCase);
        listener.handlePaymentDue(PaymentDueEvent.builder().userId(1L)
                .payload(PaymentDueEvent.Payload.builder().cardExpenseId(2L).description("d")
                        .nextDueDate(LocalDate.now()).installmentAmount(BigDecimal.ONE).currency("ARS")
                        .remainingInstallments(1).build()).build());

        // Then it delegates the mapped payment-due
        ArgumentCaptor<PaymentDue> captor = ArgumentCaptor.forClass(PaymentDue.class);
        verify(paymentDueUseCase).execute(captor.capture());
        assertThat(captor.getValue().cardExpenseId()).isEqualTo(2L);
    }

    @Test
    void financesEventListener_handlesLoanReminder() {
        // Given a loan-reminder event / When the listener handles it
        FinancesEventListener listener = new FinancesEventListener(paymentDueUseCase, loanReminderUseCase, installmentReminderUseCase);
        listener.handleLoanReminder(LoanReminderEvent.builder().userId(1L)
                .payload(LoanReminderEvent.Payload.builder().loanId(2L).loanDescription("l")
                        .nextPaymentDate(LocalDate.now()).installmentAmount(BigDecimal.ONE).currency("ARS")
                        .remainingInstallments(1).build()).build());

        // Then it delegates the mapped loan reminder
        ArgumentCaptor<LoanReminder> captor = ArgumentCaptor.forClass(LoanReminder.class);
        verify(loanReminderUseCase).execute(captor.capture());
        assertThat(captor.getValue().loanId()).isEqualTo(2L);
    }

    @Test
    void financesEventListener_handlesInstallmentReminder() {
        // Given an installment-reminder event / When the listener handles it
        FinancesEventListener listener = new FinancesEventListener(paymentDueUseCase, loanReminderUseCase, installmentReminderUseCase);
        listener.handleInstallmentReminder(InstallmentReminderEvent.builder().userId(1L)
                .payload(InstallmentReminderEvent.Payload.builder().loanId(2L).installmentId(3L)
                        .loanDescription("l").installmentNumber(1).dueDate(LocalDate.now())
                        .amount(BigDecimal.ONE).currency("ARS").build()).build());

        // Then it delegates the mapped installment reminder
        ArgumentCaptor<InstallmentReminder> captor = ArgumentCaptor.forClass(InstallmentReminder.class);
        verify(installmentReminderUseCase).execute(captor.capture());
        assertThat(captor.getValue().installmentId()).isEqualTo(3L);
    }

    @Test
    void investmentEventListener_mapsAndDelegates() {
        // Given an investment-threshold event / When the listener handles it
        new InvestmentEventListener(investmentUseCase).handleThresholdReached(InvestmentThresholdEvent.builder()
                .userId(1L).payload(InvestmentThresholdEvent.Payload.builder().holdingId(2L).ticker("AL30")
                        .name("Bond").direction("GAIN").thresholdPct(BigDecimal.ONE).actualPct(BigDecimal.TEN)
                        .currentPrice(BigDecimal.TEN).avgPurchasePrice(BigDecimal.ONE).currency("USD").build()).build());

        // Then it delegates the mapped threshold
        ArgumentCaptor<InvestmentThreshold> captor = ArgumentCaptor.forClass(InvestmentThreshold.class);
        verify(investmentUseCase).execute(captor.capture());
        assertThat(captor.getValue().ticker()).isEqualTo("AL30");
    }

    @Test
    void userEventListener_mapsAndDelegates() {
        // Given a user-registered event / When the listener handles it
        new UserEventListener(userUseCase).handleUserRegistered(UserRegisteredEvent.builder()
                .userId(1L).payload(UserRegisteredEvent.Payload.builder().email("e@x.com")
                        .firstName("Ada").lastName("L").build()).build());

        // Then it delegates the mapped registration
        ArgumentCaptor<UserRegistered> captor = ArgumentCaptor.forClass(UserRegistered.class);
        verify(userUseCase).execute(captor.capture());
        assertThat(captor.getValue().email()).isEqualTo("e@x.com");
    }
}
