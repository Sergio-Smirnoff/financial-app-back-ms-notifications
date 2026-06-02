package com.financialapp.notifications.infrastructure.persistence.mapper;

import com.financialapp.notifications.domain.model.notification.UserNotificationPreference;
import com.financialapp.notifications.infrastructure.persistence.entity.UserNotificationPreferenceSqlEntity;

public class UserNotificationPreferenceMapper {

    public static UserNotificationPreference toDomain(UserNotificationPreferenceSqlEntity entity) {
        if (entity == null)
            return null;
        return new UserNotificationPreference(
                entity.getId(),
                entity.getUserId(),
                entity.getEmail(),
                entity.isMonthlyEmailEnabled(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static UserNotificationPreferenceSqlEntity toEntity(UserNotificationPreference domain) {
        if (domain == null)
            return null;
        return UserNotificationPreferenceSqlEntity.builder()
                .id(domain.id())
                .userId(domain.userId())
                .email(domain.email())
                .monthlyEmailEnabled(domain.monthlyEmailEnabled())
                .createdAt(domain.createdAt())
                .updatedAt(domain.updatedAt())
                .build();
    }
}
