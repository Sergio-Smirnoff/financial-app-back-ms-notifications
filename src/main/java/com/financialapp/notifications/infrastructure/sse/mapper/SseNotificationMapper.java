package com.financialapp.notifications.infrastructure.sse.mapper;

import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.infrastructure.sse.dto.SseNotificationEntity;

public class SseNotificationMapper {
    public static SseNotificationEntity toEntity(Notification notification) {
        return new SseNotificationEntity(
                notification.id(),
                notification.userId(),
                notification.type().name(),
                notification.title(),
                notification.message(),
                notification.channel().name(),
                notification.read(),
                notification.metadata(),
                notification.createdAt()
        );
    }
}
