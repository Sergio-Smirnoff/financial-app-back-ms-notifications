package com.financialapp.notifications.infrastructure.persistence.mapper;

import com.financialapp.notifications.domain.model.notification.NotificationPreference;
import com.financialapp.notifications.infrastructure.persistence.entity.NotificationPreferenceSqlEntity;

public class NotificationPreferenceMapper {

    public static NotificationPreference toDomain(NotificationPreferenceSqlEntity entity) {
        if (entity == null) {
            return null;
        }
        return new NotificationPreference(
                entity.getId(),
                entity.getUserId(),
                entity.getCategory(),
                entity.isInAppEnabled(),
                entity.isEmailEnabled(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static NotificationPreferenceSqlEntity toEntity(NotificationPreference domain) {
        if (domain == null) {
            return null;
        }
        return NotificationPreferenceSqlEntity.builder()
                .id(domain.id())
                .userId(domain.userId())
                .category(domain.category())
                .inAppEnabled(domain.inAppEnabled())
                .emailEnabled(domain.emailEnabled())
                .createdAt(domain.createdAt())
                .updatedAt(domain.updatedAt())
                .build();
    }
}
