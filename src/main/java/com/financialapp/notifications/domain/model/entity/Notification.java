package com.financialapp.notifications.domain.model.entity;

import com.financialapp.notifications.domain.model.entity.enums.NotificationChannel;
import com.financialapp.notifications.domain.model.entity.enums.NotificationType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
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
        return new Notification(
                id,
                userId,
                type,
                title,
                message,
                channel,
                true,
                metadata,
                createdAt
        );
    }
}

