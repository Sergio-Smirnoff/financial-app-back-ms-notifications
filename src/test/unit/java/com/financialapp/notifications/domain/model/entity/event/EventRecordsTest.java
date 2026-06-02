package com.financialapp.notifications.domain.model.entity.event;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class EventRecordsTest {

    @Test
    void bankAlert_roundTripsAndCompares() {
        // Given a built BankAlert / Then accessors round-trip and equality holds
        BankAlert a = BankAlert.builder().userId(1L).type("LOW_BALANCE").title("t").message("m").metadata("meta").build();
        BankAlert b = BankAlert.builder().userId(1L).type("LOW_BALANCE").title("t").message("m").metadata("meta").build();
        assertThat(a.userId()).isEqualTo(1L);
        assertThat(a.type()).isEqualTo("LOW_BALANCE");
        assertThat(a.title()).isEqualTo("t");
        assertThat(a.message()).isEqualTo("m");
        assertThat(a.metadata()).isEqualTo("meta");
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a).isNotEqualTo(BankAlert.builder().userId(2L).build());
        assertThat(a.toString()).contains("BankAlert");
    }

    @Test
    void installmentReminder_roundTripsAndCompares() {
        // Given a built InstallmentReminder
        LocalDate due = LocalDate.of(2026, 5, 1);
        InstallmentReminder a = InstallmentReminder.builder().userId(1L).loanId(2L).installmentId(3L)
                .loanDescription("loan").installmentNumber(4).dueDate(due).amount(new BigDecimal("10.50"))
                .currency("ARS").build();
        InstallmentReminder b = InstallmentReminder.builder().userId(1L).loanId(2L).installmentId(3L)
                .loanDescription("loan").installmentNumber(4).dueDate(due).amount(new BigDecimal("10.50"))
                .currency("ARS").build();
        assertThat(a.loanId()).isEqualTo(2L);
        assertThat(a.installmentId()).isEqualTo(3L);
        assertThat(a.loanDescription()).isEqualTo("loan");
        assertThat(a.installmentNumber()).isEqualTo(4);
        assertThat(a.dueDate()).isEqualTo(due);
        assertThat(a.amount()).isEqualByComparingTo("10.50");
        assertThat(a.currency()).isEqualTo("ARS");
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a).isNotEqualTo(InstallmentReminder.builder().userId(9L).build());
        assertThat(a.toString()).contains("InstallmentReminder");
    }

    @Test
    void investmentThreshold_roundTripsAndCompares() {
        // Given a built InvestmentThreshold
        InvestmentThreshold a = InvestmentThreshold.builder().userId(1L).holdingId(2L).ticker("AL30").name("Bond")
                .direction("GAIN").thresholdPct(new BigDecimal("5")).actualPct(new BigDecimal("7"))
                .currentPrice(new BigDecimal("100")).avgPurchasePrice(new BigDecimal("90")).currency("USD").build();
        InvestmentThreshold b = InvestmentThreshold.builder().userId(1L).holdingId(2L).ticker("AL30").name("Bond")
                .direction("GAIN").thresholdPct(new BigDecimal("5")).actualPct(new BigDecimal("7"))
                .currentPrice(new BigDecimal("100")).avgPurchasePrice(new BigDecimal("90")).currency("USD").build();
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
        assertThat(a).isNotEqualTo(InvestmentThreshold.builder().userId(9L).build());
        assertThat(a.toString()).contains("InvestmentThreshold");
    }

    @Test
    void loanReminder_roundTripsAndCompares() {
        // Given a built LoanReminder
        LocalDate next = LocalDate.of(2026, 6, 1);
        LoanReminder a = LoanReminder.builder().userId(1L).loanId(2L).loanDescription("loan").nextPaymentDate(next)
                .installmentAmount(new BigDecimal("20")).currency("ARS").remainingInstallments(3).build();
        LoanReminder b = LoanReminder.builder().userId(1L).loanId(2L).loanDescription("loan").nextPaymentDate(next)
                .installmentAmount(new BigDecimal("20")).currency("ARS").remainingInstallments(3).build();
        assertThat(a.loanId()).isEqualTo(2L);
        assertThat(a.loanDescription()).isEqualTo("loan");
        assertThat(a.nextPaymentDate()).isEqualTo(next);
        assertThat(a.installmentAmount()).isEqualByComparingTo("20");
        assertThat(a.currency()).isEqualTo("ARS");
        assertThat(a.remainingInstallments()).isEqualTo(3);
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a).isNotEqualTo(LoanReminder.builder().userId(9L).build());
        assertThat(a.toString()).contains("LoanReminder");
    }

    @Test
    void paymentDue_roundTripsAndCompares() {
        // Given a built PaymentDue
        LocalDate next = LocalDate.of(2026, 7, 1);
        PaymentDue a = PaymentDue.builder().userId(1L).cardExpenseId(2L).description("desc").nextDueDate(next)
                .installmentAmount(new BigDecimal("30")).currency("ARS").remainingInstallments(2).build();
        PaymentDue b = PaymentDue.builder().userId(1L).cardExpenseId(2L).description("desc").nextDueDate(next)
                .installmentAmount(new BigDecimal("30")).currency("ARS").remainingInstallments(2).build();
        assertThat(a.cardExpenseId()).isEqualTo(2L);
        assertThat(a.description()).isEqualTo("desc");
        assertThat(a.nextDueDate()).isEqualTo(next);
        assertThat(a.installmentAmount()).isEqualByComparingTo("30");
        assertThat(a.currency()).isEqualTo("ARS");
        assertThat(a.remainingInstallments()).isEqualTo(2);
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a).isNotEqualTo(PaymentDue.builder().userId(9L).build());
        assertThat(a.toString()).contains("PaymentDue");
    }

    @Test
    void userRegistered_roundTripsAndCompares() {
        // Given a built UserRegistered
        UserRegistered a = UserRegistered.builder().userId(1L).email("e@x.com").firstName("Ada").lastName("Lovelace").build();
        UserRegistered b = UserRegistered.builder().userId(1L).email("e@x.com").firstName("Ada").lastName("Lovelace").build();
        assertThat(a.userId()).isEqualTo(1L);
        assertThat(a.email()).isEqualTo("e@x.com");
        assertThat(a.firstName()).isEqualTo("Ada");
        assertThat(a.lastName()).isEqualTo("Lovelace");
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a).isNotEqualTo(UserRegistered.builder().userId(9L).build());
        assertThat(a.toString()).contains("UserRegistered");
    }
}
