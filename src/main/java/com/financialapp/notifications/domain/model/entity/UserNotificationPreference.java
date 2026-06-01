package com.financialapp.notifications.domain.model.entity;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserNotificationPreference(
        Long id,
        Long userId,
        String email,
        boolean monthlyEmailEnabled,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public UserNotificationPreference withMonthlyEmailEnabled(boolean monthlyEmailEnabled) {
        return new UserNotificationPreference(
                id,
                userId,
                email,
                monthlyEmailEnabled,
                createdAt,
                LocalDateTime.now()
        );
    }

    public static UserNotificationPreference create(Long userId, String email) {
        return UserNotificationPreference.builder()
                .userId(userId)
                .email(email)
                .monthlyEmailEnabled(true)
                .build();
    }
}