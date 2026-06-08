package com.financialapp.notifications.infrastructure.persistence.mapper;

import com.financialapp.notifications.domain.model.notification.NotificationDelivery;
import com.financialapp.notifications.infrastructure.persistence.entity.NotificationDeliverySqlEntity;

public class NotificationDeliveryMapper {

    public static NotificationDeliverySqlEntity toEntity(NotificationDelivery delivery) {
        return NotificationDeliverySqlEntity.builder()
                .id(delivery.id())
                .notificationId(delivery.notificationId())
                .channel(delivery.channel())
                .status(delivery.status())
                .attempts(delivery.attempts())
                .lastError(delivery.lastError())
                .nextRetryAt(delivery.nextRetryAt())
                .build();
    }

    public static NotificationDelivery toDomain(NotificationDeliverySqlEntity entity) {
        return new NotificationDelivery(
                entity.getId(),
                entity.getNotificationId(),
                entity.getChannel(),
                entity.getStatus(),
                entity.getAttempts(),
                entity.getLastError(),
                entity.getNextRetryAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
