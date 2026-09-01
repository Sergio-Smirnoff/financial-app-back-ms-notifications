package com.financialapp.notifications.domain.model.notification;

import java.time.LocalDateTime;

public record NotificationPreference(
        Long id,
        Long userId,
        NotificationCategory category,
        boolean inAppEnabled,
        boolean emailEnabled,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public NotificationPreference withChannels(boolean inAppEnabled, boolean emailEnabled) {
        return new NotificationPreference(
                id,
                userId,
                category,
                inAppEnabled,
                emailEnabled,
                createdAt,
                LocalDateTime.now()
        );
    }

    public static NotificationPreference defaults(Long userId, NotificationCategory category) {
        boolean defaultEmail = category == NotificationCategory.SUMMARY;
        return new NotificationPreference(
                null,
                userId,
                category,
                true,
                defaultEmail,
                null,
                null
        );
    }
}
