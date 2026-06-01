package com.financialapp.notifications.infrastructure.repository.notifications.mapper;

import com.financialapp.notifications.domain.model.entity.Notification;
import com.financialapp.notifications.infrastructure.repository.notifications.NotificationSqlEntity;

public class NotificationMapper {
    public static Notification toDomain(NotificationSqlEntity entity) {
        return Notification.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .type(entity.getType())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .channel(entity.getChannel())
                .read(entity.isRead())
                .metadata(entity.getMetadata())
                .createdAt(entity.getCreatedAt())
                .build();
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
