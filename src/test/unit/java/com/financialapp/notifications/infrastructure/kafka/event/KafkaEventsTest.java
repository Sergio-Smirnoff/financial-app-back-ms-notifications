package com.financialapp.notifications.infrastructure.kafka.event;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaEventsTest {

    @Test
    void bankAlertEvent_buildsAndExposesFields() {
        // Given / When built
        BankAlertEvent e = BankAlertEvent.builder().userId(1L).type("LOW_BALANCE").title("t").message("m").metadata("meta").build();

        // Then accessors and equals/toString behave (Lombok @Data)
        assertThat(e.getUserId()).isEqualTo(1L);
        assertThat(e.getType()).isEqualTo("LOW_BALANCE");
        assertThat(e.getTitle()).isEqualTo("t");
        assertThat(e.getMessage()).isEqualTo("m");
        assertThat(e.getMetadata()).isEqualTo("meta");
        assertThat(e).isEqualTo(BankAlertEvent.builder().userId(1L).type("LOW_BALANCE").title("t").message("m").metadata("meta").build());
        assertThat(e.toString()).contains("BankAlertEvent");
    }

    @Test
    void installmentReminderEvent_defaultsTypeAndTimestamp_andExposesPayload() {
        // Given a payload / When built with defaults
        InstallmentReminderEvent.Payload payload = InstallmentReminderEvent.Payload.builder()
                .loanId(2L).installmentId(3L).loanDescription("loan").installmentNumber(4)
                .dueDate(LocalDate.of(2026, 5, 1)).amount(new BigDecimal("10")).currency("ARS").build();
        InstallmentReminderEvent e = InstallmentReminderEvent.builder().userId(1L).payload(payload).build();

        // Then defaults are applied and payload accessors expose fields
        assertThat(e.getEventType()).isEqualTo("INSTALLMENT_REMINDER");
        assertThat(e.getTimestamp()).isNotNull();
        assertThat(e.getUserId()).isEqualTo(1L);
        assertThat(e.getPayload().getLoanId()).isEqualTo(2L);
        assertThat(e.getPayload().getInstallmentId()).isEqualTo(3L);
        assertThat(e.getPayload().getLoanDescription()).isEqualTo("loan");
        assertThat(e.getPayload().getInstallmentNumber()).isEqualTo(4);
        assertThat(e.getPayload().getDueDate()).isEqualTo(LocalDate.of(2026, 5, 1));
        assertThat(e.getPayload().getAmount()).isEqualByComparingTo("10");
        assertThat(e.getPayload().getCurrency()).isEqualTo("ARS");
    }

    @Test
    void investmentThresholdEvent_defaultsAndPayload() {
        // Given a payload / When built
        InvestmentThresholdEvent.Payload payload = InvestmentThresholdEvent.Payload.builder()
                .holdingId(2L).ticker("AL30").name("Bond").direction("GAIN").thresholdPct(new BigDecimal("5"))
                .actualPct(new BigDecimal("7")).currentPrice(new BigDecimal("100"))
                .avgPurchasePrice(new BigDecimal("90")).currency("USD").build();
        InvestmentThresholdEvent e = InvestmentThresholdEvent.builder().userId(1L)
                .timestamp(Instant.EPOCH).payload(payload).build();

        // Then defaults/overrides and payload accessors expose fields
        assertThat(e.getEventType()).isEqualTo("INVESTMENT_THRESHOLD");
        assertThat(e.getTimestamp()).isEqualTo(Instant.EPOCH);
        assertThat(e.getPayload().getHoldingId()).isEqualTo(2L);
        assertThat(e.getPayload().getTicker()).isEqualTo("AL30");
        assertThat(e.getPayload().getName()).isEqualTo("Bond");
        assertThat(e.getPayload().getDirection()).isEqualTo("GAIN");
        assertThat(e.getPayload().getThresholdPct()).isEqualByComparingTo("5");
        assertThat(e.getPayload().getActualPct()).isEqualByComparingTo("7");
        assertThat(e.getPayload().getCurrentPrice()).isEqualByComparingTo("100");
        assertThat(e.getPayload().getAvgPurchasePrice()).isEqualByComparingTo("90");
        assertThat(e.getPayload().getCurrency()).isEqualTo("USD");
    }

    @Test
    void loanReminderEvent_defaultsAndPayload() {
        // Given a payload / When built
        LoanReminderEvent.Payload payload = LoanReminderEvent.Payload.builder()
                .loanId(2L).loanDescription("loan").nextPaymentDate(LocalDate.of(2026, 6, 1))
                .installmentAmount(new BigDecimal("20")).currency("ARS").remainingInstallments(3).build();
        LoanReminderEvent e = LoanReminderEvent.builder().userId(1L).payload(payload).build();

        // Then defaults and payload accessors expose fields
        assertThat(e.getEventType()).isEqualTo("LOAN_REMINDER");
        assertThat(e.getTimestamp()).isNotNull();
        assertThat(e.getPayload().getLoanId()).isEqualTo(2L);
        assertThat(e.getPayload().getLoanDescription()).isEqualTo("loan");
        assertThat(e.getPayload().getNextPaymentDate()).isEqualTo(LocalDate.of(2026, 6, 1));
        assertThat(e.getPayload().getInstallmentAmount()).isEqualByComparingTo("20");
        assertThat(e.getPayload().getCurrency()).isEqualTo("ARS");
        assertThat(e.getPayload().getRemainingInstallments()).isEqualTo(3);
    }

    @Test
    void paymentDueEvent_defaultsAndPayload() {
        // Given a payload / When built
        PaymentDueEvent.Payload payload = PaymentDueEvent.Payload.builder()
                .cardExpenseId(2L).description("desc").nextDueDate(LocalDate.of(2026, 7, 1))
                .installmentAmount(new BigDecimal("30")).currency("ARS").remainingInstallments(2).build();
        PaymentDueEvent e = PaymentDueEvent.builder().userId(1L).payload(payload).build();

        // Then defaults and payload accessors expose fields
        assertThat(e.getEventType()).isEqualTo("PAYMENT_DUE");
        assertThat(e.getTimestamp()).isNotNull();
        assertThat(e.getPayload().getCardExpenseId()).isEqualTo(2L);
        assertThat(e.getPayload().getDescription()).isEqualTo("desc");
        assertThat(e.getPayload().getNextDueDate()).isEqualTo(LocalDate.of(2026, 7, 1));
        assertThat(e.getPayload().getInstallmentAmount()).isEqualByComparingTo("30");
        assertThat(e.getPayload().getCurrency()).isEqualTo("ARS");
        assertThat(e.getPayload().getRemainingInstallments()).isEqualTo(2);
    }

    @Test
    void userRegisteredEvent_defaultsAndPayload() {
        // Given a payload / When built
        UserRegisteredEvent.Payload payload = UserRegisteredEvent.Payload.builder()
                .email("e@x.com").firstName("Ada").lastName("L").build();
        UserRegisteredEvent e = UserRegisteredEvent.builder().userId(1L).payload(payload).build();

        // Then defaults and payload accessors expose fields
        assertThat(e.getEventType()).isEqualTo("USER_REGISTERED");
        assertThat(e.getTimestamp()).isNotNull();
        assertThat(e.getPayload().getEmail()).isEqualTo("e@x.com");
        assertThat(e.getPayload().getFirstName()).isEqualTo("Ada");
        assertThat(e.getPayload().getLastName()).isEqualTo("L");
    }
}
