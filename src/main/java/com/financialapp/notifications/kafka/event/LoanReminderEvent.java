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
public class LoanReminderEvent {

    @Builder.Default
    private String eventType = "LOAN_REMINDER";
    private Long userId;
    @Builder.Default
    private Instant timestamp = Instant.now();
    private Payload payload;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Payload {
        private Long loanId;
        private String loanDescription;
        private LocalDate nextPaymentDate;
        private BigDecimal installmentAmount;
        private String currency;
        private int remainingInstallments;
    }
}
