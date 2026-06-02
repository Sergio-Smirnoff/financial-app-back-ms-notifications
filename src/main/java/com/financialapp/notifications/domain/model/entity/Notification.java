package com.financialapp.notifications.domain.model.entity;

import com.financialapp.notifications.domain.model.entity.enums.NotificationChannel;
import com.financialapp.notifications.domain.model.entity.enums.NotificationType;

import java.time.LocalDateTime;

public record Notification(
        Long id,
        Long userId,
        NotificationType type,
        String title,
        String message,
        NotificationChannel channel,
        boolean read,
        String metadata,
        LocalDateTime createdAt
) {
    public Notification markAsRead() {
        return new Notification(id, userId, type, title, message, channel, true, metadata, createdAt);
    }

    public static Notification create(
            Long userId,
            NotificationType type,
            String title,
            String message,
            NotificationChannel channel,
            String metadata
    ) {
        return new Notification(null, userId, type, title, message, channel, false, metadata, null);
    }
}
