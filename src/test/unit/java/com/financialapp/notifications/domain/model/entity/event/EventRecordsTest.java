package com.financialapp.notifications.domain.model.entity.event;

import com.financialapp.notifications.domain.event.BankAlert;
import com.financialapp.notifications.domain.event.InstallmentReminder;
import com.financialapp.notifications.domain.event.InvestmentThreshold;
import com.financialapp.notifications.domain.event.LoanReminder;
import com.financialapp.notifications.domain.event.PaymentDue;
import com.financialapp.notifications.domain.event.UserRegistered;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class EventRecordsTest {

    @Test
    void bankAlert_roundTripsAndCompares() {
        // Given a built BankAlert / Then accessors round-trip and equality holds
        BankAlert a = new BankAlert(1L, "LOW_BALANCE", "t", "m", "meta");
        BankAlert b = new BankAlert(1L, "LOW_BALANCE", "t", "m", "meta");
        assertThat(a.userId()).isEqualTo(1L);
        assertThat(a.type()).isEqualTo("LOW_BALANCE");
        assertThat(a.title()).isEqualTo("t");
        assertThat(a.message()).isEqualTo("m");
        assertThat(a.metadata()).isEqualTo("meta");
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a).isNotEqualTo(new BankAlert(2L, null, null, null, null));
        assertThat(a.toString()).contains("BankAlert");
    }

    @Test
    void installmentReminder_roundTripsAndCompares() {
        // Given a built InstallmentReminder
        LocalDate due = LocalDate.of(2026, 5, 1);
        InstallmentReminder a = new InstallmentReminder(1L, 2L, 3L, "loan", 4, due, new BigDecimal("10.50"), "ARS");
        InstallmentReminder b = new InstallmentReminder(1L, 2L, 3L, "loan", 4, due, new BigDecimal("10.50"), "ARS");
        assertThat(a.loanId()).isEqualTo(2L);
        assertThat(a.installmentId()).isEqualTo(3L);
        assertThat(a.loanDescription()).isEqualTo("loan");
        assertThat(a.installmentNumber()).isEqualTo(4);
        assertThat(a.dueDate()).isEqualTo(due);
        assertThat(a.amount()).isEqualByComparingTo("10.50");
        assertThat(a.currency()).isEqualTo("ARS");
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a).isNotEqualTo(new InstallmentReminder(9L, null, null, null, 0, null, null, null));
        assertThat(a.toString()).contains("InstallmentReminder");
    }

    @Test
    void investmentThreshold_roundTripsAndCompares() {
        // Given a built InvestmentThreshold
        InvestmentThreshold a = new InvestmentThreshold(1L, 2L, "AL30", "Bond", "GAIN",
                new BigDecimal("5"), new BigDecimal("7"),
                new BigDecimal("100"), new BigDecimal("90"), "USD");
        InvestmentThreshold b = new InvestmentThreshold(1L, 2L, "AL30", "Bond", "GAIN",
                new BigDecimal("5"), new BigDecimal("7"),
                new BigDecimal("100"), new BigDecimal("90"), "USD");
        assertThat(a.holdingId()).isEqualTo(2L);
        assertThat(a.ticker()).isEqualTo("AL30");
        assertThat(a.name()).isEqualTo("Bond");
        assertThat(a.direction()).isEqualTo("GAIN");
        assertThat(a.thresholdPct()).isEqualByComparingTo("5");
        assertThat(a.actualPct()).isEqualByComparingTo("7");
        assertThat(a.currentPrice()).isEqualByComparingTo("100");
        assertThat(a.avgPurchasePrice()).isEqualByComparingTo("90");
        assertThat(a.currency()).isEqualTo("USD");
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a).isNotEqualTo(new InvestmentThreshold(9L, null, null, null, null, null, null, null, null, null));
        assertThat(a.toString()).contains("InvestmentThreshold");
    }

    @Test
    void loanReminder_roundTripsAndCompares() {
        // Given a built LoanReminder
        LocalDate next = LocalDate.of(2026, 6, 1);
        LoanReminder a = new LoanReminder(1L, 2L, "loan", next, new BigDecimal("20"), "ARS", 3);
        LoanReminder b = new LoanReminder(1L, 2L, "loan", next, new BigDecimal("20"), "ARS", 3);
        assertThat(a.loanId()).isEqualTo(2L);
        assertThat(a.loanDescription()).isEqualTo("loan");
        assertThat(a.nextPaymentDate()).isEqualTo(next);
        assertThat(a.installmentAmount()).isEqualByComparingTo("20");
        assertThat(a.currency()).isEqualTo("ARS");
        assertThat(a.remainingInstallments()).isEqualTo(3);
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a).isNotEqualTo(new LoanReminder(9L, null, null, null, null, null, 0));
        assertThat(a.toString()).contains("LoanReminder");
    }

    @Test
    void paymentDue_roundTripsAndCompares() {
        // Given a built PaymentDue
        LocalDate next = LocalDate.of(2026, 7, 1);
        PaymentDue a = new PaymentDue(1L, 2L, "desc", next, new BigDecimal("30"), "ARS", 2);
        PaymentDue b = new PaymentDue(1L, 2L, "desc", next, new BigDecimal("30"), "ARS", 2);
        assertThat(a.cardExpenseId()).isEqualTo(2L);
        assertThat(a.description()).isEqualTo("desc");
        assertThat(a.nextDueDate()).isEqualTo(next);
        assertThat(a.installmentAmount()).isEqualByComparingTo("30");
        assertThat(a.currency()).isEqualTo("ARS");
        assertThat(a.remainingInstallments()).isEqualTo(2);
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a).isNotEqualTo(new PaymentDue(9L, null, null, null, null, null, 0));
        assertThat(a.toString()).contains("PaymentDue");
    }

    @Test
    void userRegistered_roundTripsAndCompares() {
        // Given a built UserRegistered
        UserRegistered a = new UserRegistered(1L, "e@x.com", "Ada", "Lovelace");
        UserRegistered b = new UserRegistered(1L, "e@x.com", "Ada", "Lovelace");
        assertThat(a.userId()).isEqualTo(1L);
        assertThat(a.email()).isEqualTo("e@x.com");
        assertThat(a.firstName()).isEqualTo("Ada");
        assertThat(a.lastName()).isEqualTo("Lovelace");
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a).isNotEqualTo(new UserRegistered(9L, null, null, null));
        assertThat(a.toString()).contains("UserRegistered");
    }
}
