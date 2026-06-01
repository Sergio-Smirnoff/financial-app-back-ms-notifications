package com.financialapp.notifications.infrastructure.repository.preferences.mapper;

import com.financialapp.notifications.domain.model.entity.UserNotificationPreference;
import com.financialapp.notifications.infrastructure.repository.preferences.UserNotificationPreferenceSqlEntity;

public class UserNotificationPreferenceMapper {

    public static UserNotificationPreference toDomain(UserNotificationPreferenceSqlEntity entity) {
        if (entity == null) return null;
        return UserNotificationPreference.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .email(entity.getEmail())
                .monthlyEmailEnabled(entity.isMonthlyEmailEnabled())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static UserNotificationPreferenceSqlEntity toEntity(UserNotificationPreference domain) {
        if (domain == null) return null;
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
