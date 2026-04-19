package com.financialapp.notifications.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAlertEvent {
    private Long userId;
    private String type; // CARD_EXPIRING, LOW_BALANCE, etc.
    private String title;
    private String message;
    private String metadata;
}
