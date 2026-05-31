package com.financialapp.notifications.infrastructure.kafka.mapper;

import com.financialapp.notifications.infrastructure.kafka.event.BankAlertEvent;
import com.financialapp.notifications.domain.model.entity.event.BankAlert;

public class BankAlertMapper {
    public static BankAlert toDomain(BankAlertEvent event) {
        return BankAlert.builder()
                .userId(event.getUserId())
                .type(event.getType())
                .title(event.getTitle())
                .message(event.getMessage())
                .metadata(event.getMetadata())
                .build();
    }
}
