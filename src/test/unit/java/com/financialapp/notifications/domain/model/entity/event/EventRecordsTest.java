package com.financialapp.notifications.domain.model.entity.event;

import com.financialapp.notifications.domain.event.BalanceAdjusted;
import com.financialapp.notifications.domain.event.BankAlert;
import com.financialapp.notifications.domain.event.CardExpiring;
import com.financialapp.notifications.domain.event.CardInstallmentDue;
import com.financialapp.notifications.domain.event.InstallmentReminder;
import com.financialapp.notifications.domain.event.InvestmentThreshold;
import com.financialapp.notifications.domain.event.LoanReminder;
import com.financialapp.notifications.domain.event.LowBalance;
import com.financialapp.notifications.domain.event.PaymentDue;
import com.financialapp.notifications.domain.event.UserRegistered;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class EventRecordsTest {

    @Test
    void bankAlert_roundTripsAndCompares() {
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
        assertThat(a.toString()).contains("InstallmentReminder");
    }

    @Test
    void investmentThreshold_roundTripsAndCompares() {
        InvestmentThreshold a = new InvestmentThreshold(1L, 2L, "AL30", "Bond", "GAIN",
                new BigDecimal("5"), new BigDecimal("7"),
                new BigDecimal("100"), new BigDecimal("90"), "USD");
        InvestmentThreshold b = new InvestmentThreshold(1L, 2L, "AL30", "Bond", "GAIN",
                new BigDecimal("5"), new BigDecimal("7"),
                new BigDecimal("100"), new BigDecimal("90"), "USD");
        assertThat(a.holdingId()).isEqualTo(2L);
        assertThat(a.ticker()).isEqualTo("AL30");
        assertThat(a.direction()).isEqualTo("GAIN");
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a.toString()).contains("InvestmentThreshold");
    }

    @Test
    void loanReminder_roundTripsAndCompares() {
        LocalDate due = LocalDate.of(2026, 7, 1);
        LoanReminder a = new LoanReminder(1L, 2L, 3L, "Car Loan", 4, due);
        LoanReminder b = new LoanReminder(1L, 2L, 3L, "Car Loan", 4, due);
        assertThat(a.loanId()).isEqualTo(2L);
        assertThat(a.installmentId()).isEqualTo(3L);
        assertThat(a.loanName()).isEqualTo("Car Loan");
        assertThat(a.installmentNumber()).isEqualTo(4);
        assertThat(a.dueDate()).isEqualTo(due);
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a.toString()).contains("LoanReminder");
    }

    @Test
    void paymentDue_roundTripsAndCompares() {
        LocalDate due = LocalDate.of(2026, 8, 15);
        PaymentDue a = new PaymentDue(1L, "4111111111111234", 5L, 2, 12, "TV Purchase", due,
                new BigDecimal("150"), "ARS");
        PaymentDue b = new PaymentDue(1L, "4111111111111234", 5L, 2, 12, "TV Purchase", due,
                new BigDecimal("150"), "ARS");
        assertThat(a.userId()).isEqualTo(1L);
        assertThat(a.cardNumber()).isEqualTo("4111111111111234");
        assertThat(a.installmentNumber()).isEqualTo(2);
        assertThat(a.totalInstallments()).isEqualTo(12);
        assertThat(a.description()).isEqualTo("TV Purchase");
        assertThat(a.dueDate()).isEqualTo(due);
        assertThat(a.amount()).isEqualByComparingTo("150");
        assertThat(a.currency()).isEqualTo("ARS");
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a.toString()).contains("PaymentDue");
    }

    @Test
    void userRegistered_roundTripsAndCompares() {
        UserRegistered a = new UserRegistered(1L, "e@x.com", "Ada", "Lovelace");
        UserRegistered b = new UserRegistered(1L, "e@x.com", "Ada", "Lovelace");
        assertThat(a.userId()).isEqualTo(1L);
        assertThat(a.email()).isEqualTo("e@x.com");
        assertThat(a.firstName()).isEqualTo("Ada");
        assertThat(a.lastName()).isEqualTo("Lovelace");
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a.toString()).contains("UserRegistered");
    }

    @Test
    void lowBalance_roundTripsAndCompares() {
        LowBalance a = new LowBalance(1L, "Savings", "001", "BANCO", new BigDecimal("50"), "ARS");
        LowBalance b = new LowBalance(1L, "Savings", "001", "BANCO", new BigDecimal("50"), "ARS");
        assertThat(a.userId()).isEqualTo(1L);
        assertThat(a.accountName()).isEqualTo("Savings");
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a.toString()).contains("LowBalance");
    }

    @Test
    void balanceAdjusted_roundTripsAndCompares() {
        BalanceAdjusted a = new BalanceAdjusted(1L, "Checking", "002", "BANCO", new BigDecimal("100"), "ARS", true);
        BalanceAdjusted b = new BalanceAdjusted(1L, "Checking", "002", "BANCO", new BigDecimal("100"), "ARS", true);
        assertThat(a.userId()).isEqualTo(1L);
        assertThat(a.accountName()).isEqualTo("Checking");
        assertThat(a.credit()).isTrue();
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a.toString()).contains("BalanceAdjusted");
    }

    @Test
    void cardExpiring_roundTripsAndCompares() {
        CardExpiring a = new CardExpiring(1L, "4111111111111234", "BANK01", "2027-12");
        CardExpiring b = new CardExpiring(1L, "4111111111111234", "BANK01", "2027-12");
        assertThat(a.userId()).isEqualTo(1L);
        assertThat(a.cardNumber()).isEqualTo("4111111111111234");
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a.toString()).contains("CardExpiring");
    }

    @Test
    void cardInstallmentDue_roundTripsAndCompares() {
        LocalDate due = LocalDate.of(2026, 8, 15);
        CardInstallmentDue a = new CardInstallmentDue(1L, "4111111111111234", 5L, 2, 12,
                "TV", due, new BigDecimal("150"), "ARS");
        CardInstallmentDue b = new CardInstallmentDue(1L, "4111111111111234", 5L, 2, 12,
                "TV", due, new BigDecimal("150"), "ARS");
        assertThat(a.userId()).isEqualTo(1L);
        assertThat(a.installmentNumber()).isEqualTo(2);
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a.toString()).contains("CardInstallmentDue");
    }
}
