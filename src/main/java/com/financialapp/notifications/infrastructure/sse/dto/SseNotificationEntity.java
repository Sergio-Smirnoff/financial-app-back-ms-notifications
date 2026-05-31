package com.financialapp.notifications.infrastructure.sse.dto;

import java.time.LocalDateTime;

public record SseNotificationEntity(
        Long id,
        Long userId,
        String type,
        String title,
        String message,
        String channel,
        boolean read,
        String metadata,
        LocalDateTime createdAt
) {}
