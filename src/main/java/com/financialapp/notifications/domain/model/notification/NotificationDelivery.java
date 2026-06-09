package com.financialapp.notifications.domain.model.notification;

import java.time.LocalDateTime;

public record NotificationDelivery(
        Long id,
        Long notificationId,
        NotificationChannel channel,
        DeliveryStatus status,
        int attempts,
        String lastError,
        LocalDateTime nextRetryAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static NotificationDelivery pending(Long notificationId, NotificationChannel channel) {
        return new NotificationDelivery(null, notificationId, channel,
                DeliveryStatus.PENDING, 0, null, null, null, null);
    }

    public NotificationDelivery markSent() {
        return new NotificationDelivery(id, notificationId, channel,
                DeliveryStatus.SENT, attempts + 1, null, null, createdAt, LocalDateTime.now());
    }

    public NotificationDelivery markFailed(String error, LocalDateTime nextRetry) {
        return new NotificationDelivery(id, notificationId, channel,
                DeliveryStatus.FAILED, attempts + 1, error, nextRetry, createdAt, LocalDateTime.now());
    }
}
