package com.financialapp.notifications.infrastructure.messaging.mapper;

import com.financialapp.notifications.infrastructure.messaging.payload.BankAlertEvent;
import com.financialapp.notifications.domain.event.BankAlert;

public class BankAlertMapper {
    public static BankAlert toDomain(BankAlertEvent event) {
        return new BankAlert(
                event.getUserId(),
                event.getType(),
                event.getTitle(),
                event.getMessage(),
                event.getMetadata()
        );
    }
}
