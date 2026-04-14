package com.financialapp.notifications.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDueEvent {

    @Builder.Default
    private String eventType = "PAYMENT_DUE";
    private Long userId;
    @Builder.Default
    private Instant timestamp = Instant.now();
    private Payload payload;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Payload {
        private Long cardExpenseId;
        private String description;
        private LocalDate nextDueDate;
        private BigDecimal installmentAmount;
        private String currency;
        private int remainingInstallments;
    }
}
