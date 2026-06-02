package com.financialapp.notifications.infrastructure.kafka.mapper;

import com.financialapp.notifications.infrastructure.kafka.event.UserRegisteredEvent;
import com.financialapp.notifications.domain.model.entity.event.UserRegistered;

public class UserRegisteredMapper {
    public static UserRegistered toDomain(UserRegisteredEvent event) {
        UserRegisteredEvent.Payload p = event.getPayload();
        return new UserRegistered(
                event.getUserId(),
                p.getEmail(),
                p.getFirstName(),
                p.getLastName()
        );
    }
}
