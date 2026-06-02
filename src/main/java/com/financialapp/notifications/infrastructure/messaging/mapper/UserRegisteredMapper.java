package com.financialapp.notifications.infrastructure.messaging.mapper;

import com.financialapp.notifications.infrastructure.messaging.payload.UserRegisteredEvent;
import com.financialapp.notifications.domain.event.UserRegistered;

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
