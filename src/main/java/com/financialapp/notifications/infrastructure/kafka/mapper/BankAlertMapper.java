package com.financialapp.notifications.infrastructure.kafka.mapper;

import com.financialapp.notifications.infrastructure.kafka.event.BankAlertEvent;
import com.financialapp.notifications.domain.model.entity.event.BankAlert;

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
