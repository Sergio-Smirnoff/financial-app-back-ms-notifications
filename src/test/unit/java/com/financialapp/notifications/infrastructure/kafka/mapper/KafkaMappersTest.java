package com.financialapp.notifications.infrastructure.kafka.mapper;

import com.financialapp.notifications.domain.model.entity.event.BankAlert;
import com.financialapp.notifications.domain.model.entity.event.InstallmentReminder;
import com.financialapp.notifications.domain.model.entity.event.InvestmentThreshold;
import com.financialapp.notifications.domain.model.entity.event.LoanReminder;
import com.financialapp.notifications.domain.model.entity.event.PaymentDue;
import com.financialapp.notifications.domain.model.entity.event.UserRegistered;
import com.financialapp.notifications.infrastructure.kafka.event.BankAlertEvent;
import com.financialapp.notifications.infrastructure.kafka.event.InstallmentReminderEvent;
import com.financialapp.notifications.infrastructure.kafka.event.InvestmentThresholdEvent;
import com.financialapp.notifications.infrastructure.kafka.event.LoanReminderEvent;
import com.financialapp.notifications.infrastructure.kafka.event.PaymentDueEvent;
import com.financialapp.notifications.infrastructure.kafka.event.UserRegisteredEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaMappersTest {

    @Test
    void mapperUtilities_areInstantiable() {
        // Given the static mapper utilities / When instantiated (covers the implicit constructors)
        // Then construction succeeds
        assertThat(new BankAlertMapper()).isNotNull();
        assertThat(new InstallmentReminderMapper()).isNotNull();
        assertThat(new InvestmentThresholdMapper()).isNotNull();
        assertThat(new LoanReminderMapper()).isNotNull();
        assertThat(new PaymentDueMapper()).isNotNull();
        assertThat(new UserRegisteredMapper()).isNotNull();
    }

    @Test
    void bankAlertMapper_copiesFlatFields() {
        // Given a bank-alert event / When mapped
        BankAlert domain = BankAlertMapper.toDomain(BankAlertEvent.builder()
                .userId(1L).type("LOW_BALANCE").title("t").message("m").metadata("meta").build());

        // Then every field is copied
        assertThat(domain.userId()).isEqualTo(1L);
        assertThat(domain.type()).isEqualTo("LOW_BALANCE");
        assertThat(domain.title()).isEqualTo("t");
        assertThat(domain.message()).isEqualTo("m");
        assertThat(domain.metadata()).isEqualTo("meta");
    }

    @Test
    void installmentReminderMapper_flattensPayload() {
        // Given an installment-reminder event with payload / When mapped
        LocalDate due = LocalDate.of(2026, 5, 1);
        InstallmentReminder domain = InstallmentReminderMapper.toDomain(InstallmentReminderEvent.builder()
                .userId(1L).payload(InstallmentReminderEvent.Payload.builder()
                        .loanId(2L).installmentId(3L).loanDescription("loan").installmentNumber(4)
                        .dueDate(due).amount(new BigDecimal("10")).currency("ARS").build())
                .build());

        // Then header userId and payload fields are flattened
        assertThat(domain.userId()).isEqualTo(1L);
        assertThat(domain.loanId()).isEqualTo(2L);
        assertThat(domain.installmentId()).isEqualTo(3L);
        assertThat(domain.loanDescription()).isEqualTo("loan");
        assertThat(domain.installmentNumber()).isEqualTo(4);
        assertThat(domain.dueDate()).isEqualTo(due);
        assertThat(domain.amount()).isEqualByComparingTo("10");
        assertThat(domain.currency()).isEqualTo("ARS");
    }

    @Test
    void investmentThresholdMapper_flattensPayload() {
        // Given an investment-threshold event with payload / When mapped
        InvestmentThreshold domain = InvestmentThresholdMapper.toDomain(InvestmentThresholdEvent.builder()
                .userId(1L).payload(InvestmentThresholdEvent.Payload.builder()
                        .holdingId(2L).ticker("AL30").name("Bond").direction("GAIN")
                        .thresholdPct(new BigDecimal("5")).actualPct(new BigDecimal("7"))
                        .currentPrice(new BigDecimal("100")).avgPurchasePrice(new BigDecimal("90"))
                        .currency("USD").build())
                .build());

        // Then header userId and payload fields are flattened
        assertThat(domain.userId()).isEqualTo(1L);
        assertThat(domain.holdingId()).isEqualTo(2L);
        assertThat(domain.ticker()).isEqualTo("AL30");
        assertThat(domain.name()).isEqualTo("Bond");
        assertThat(domain.direction()).isEqualTo("GAIN");
        assertThat(domain.thresholdPct()).isEqualByComparingTo("5");
        assertThat(domain.actualPct()).isEqualByComparingTo("7");
        assertThat(domain.currentPrice()).isEqualByComparingTo("100");
        assertThat(domain.avgPurchasePrice()).isEqualByComparingTo("90");
        assertThat(domain.currency()).isEqualTo("USD");
    }

    @Test
    void loanReminderMapper_flattensPayload() {
        // Given a loan-reminder event with payload / When mapped
        LocalDate next = LocalDate.of(2026, 6, 1);
        LoanReminder domain = LoanReminderMapper.toDomain(LoanReminderEvent.builder()
                .userId(1L).payload(LoanReminderEvent.Payload.builder()
                        .loanId(2L).loanDescription("loan").nextPaymentDate(next)
                        .installmentAmount(new BigDecimal("20")).currency("ARS").remainingInstallments(3).build())
                .build());

        // Then header userId and payload fields are flattened
        assertThat(domain.userId()).isEqualTo(1L);
        assertThat(domain.loanId()).isEqualTo(2L);
        assertThat(domain.loanDescription()).isEqualTo("loan");
        assertThat(domain.nextPaymentDate()).isEqualTo(next);
        assertThat(domain.installmentAmount()).isEqualByComparingTo("20");
        assertThat(domain.currency()).isEqualTo("ARS");
        assertThat(domain.remainingInstallments()).isEqualTo(3);
    }

    @Test
    void paymentDueMapper_flattensPayload() {
        // Given a payment-due event with payload / When mapped
        LocalDate next = LocalDate.of(2026, 7, 1);
        PaymentDue domain = PaymentDueMapper.toDomain(PaymentDueEvent.builder()
                .userId(1L).payload(PaymentDueEvent.Payload.builder()
                        .cardExpenseId(2L).description("desc").nextDueDate(next)
                        .installmentAmount(new BigDecimal("30")).currency("ARS").remainingInstallments(2).build())
                .build());

        // Then header userId and payload fields are flattened
        assertThat(domain.userId()).isEqualTo(1L);
        assertThat(domain.cardExpenseId()).isEqualTo(2L);
        assertThat(domain.description()).isEqualTo("desc");
        assertThat(domain.nextDueDate()).isEqualTo(next);
        assertThat(domain.installmentAmount()).isEqualByComparingTo("30");
        assertThat(domain.currency()).isEqualTo("ARS");
        assertThat(domain.remainingInstallments()).isEqualTo(2);
    }

    @Test
    void userRegisteredMapper_flattensPayload() {
        // Given a user-registered event with payload / When mapped
        UserRegistered domain = UserRegisteredMapper.toDomain(UserRegisteredEvent.builder()
                .userId(1L).payload(UserRegisteredEvent.Payload.builder()
                        .email("e@x.com").firstName("Ada").lastName("L").build())
                .build());

        // Then header userId and payload fields are flattened
        assertThat(domain.userId()).isEqualTo(1L);
        assertThat(domain.email()).isEqualTo("e@x.com");
        assertThat(domain.firstName()).isEqualTo("Ada");
        assertThat(domain.lastName()).isEqualTo("L");
    }
}
