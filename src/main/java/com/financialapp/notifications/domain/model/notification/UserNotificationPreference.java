package com.financialapp.notifications.domain.model.notification;

import java.time.LocalDateTime;

public record UserNotificationPreference(
        Long id,
        Long userId,
        String email,
        boolean monthlyEmailEnabled,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public UserNotificationPreference withMonthlyEmailEnabled(boolean monthlyEmailEnabled) {
        return new UserNotificationPreference(
                id,
                userId,
                email,
                monthlyEmailEnabled,
                createdAt,
                LocalDateTime.now()
        );
    }

    public static UserNotificationPreference create(Long userId, String email) {
        return new UserNotificationPreference(null, userId, email, true, null, null);
    }
}
