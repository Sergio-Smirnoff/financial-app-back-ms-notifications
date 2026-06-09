package com.financialapp.notifications.infrastructure.messaging.mapper;

import com.financialapp.notifications.domain.event.UserRegistered;
import com.financialapp.notifications.infrastructure.messaging.payload.UserRegisteredData;

public class UserRegisteredMapper {

    public static UserRegistered toDomain(UserRegisteredData data) {
        return new UserRegistered(
                data.userId(),
                data.email(),
                data.firstName(),
                data.lastName()
        );
    }
}
