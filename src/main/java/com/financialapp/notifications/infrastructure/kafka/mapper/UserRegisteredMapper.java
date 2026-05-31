package com.financialapp.notifications.infrastructure.kafka.mapper;

import com.financialapp.notifications.infrastructure.kafka.event.UserRegisteredEvent;
import com.financialapp.notifications.domain.model.entity.event.UserRegistered;

public class UserRegisteredMapper {
    public static UserRegistered toDomain(UserRegisteredEvent event) {
        UserRegisteredEvent.Payload p = event.getPayload();
        return UserRegistered.builder()
                .userId(event.getUserId())
                .email(p.getEmail())
                .firstName(p.getFirstName())
                .lastName(p.getLastName())
                .build();
    }
}
