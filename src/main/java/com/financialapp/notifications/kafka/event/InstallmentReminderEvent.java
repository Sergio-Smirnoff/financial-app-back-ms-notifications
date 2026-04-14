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
public class InstallmentReminderEvent {

    @Builder.Default
    private String eventType = "INSTALLMENT_REMINDER";
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
        private Long installmentId;
        private String loanDescription;
        private int installmentNumber;
        private LocalDate dueDate;
        private BigDecimal amount;
        private String currency;
    }
}
