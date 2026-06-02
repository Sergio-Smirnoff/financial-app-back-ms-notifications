package com.financialapp.notifications.infrastructure.persistence.mapper;

import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.infrastructure.persistence.entity.NotificationSqlEntity;

public class NotificationMapper {
    public static Notification toDomain(NotificationSqlEntity entity) {
        return new Notification(
                entity.getId(),
                entity.getUserId(),
                entity.getType(),
                entity.getTitle(),
                entity.getMessage(),
                entity.getChannel(),
                entity.isRead(),
                entity.getMetadata(),
                entity.getCreatedAt());
    }

    public static NotificationSqlEntity toEntity(Notification notification) {
        return NotificationSqlEntity.builder()
                .id(notification.id())
                .userId(notification.userId())
                .type(notification.type())
                .title(notification.title())
                .message(notification.message())
                .channel(notification.channel())
                .read(notification.read())
                .metadata(notification.metadata())
                .createdAt(notification.createdAt())
                .build();
    }
}
