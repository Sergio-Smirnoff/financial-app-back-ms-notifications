package com.financialapp.notifications.domain.repository;

import com.financialapp.notifications.domain.model.notification.NotificationCategory;
import com.financialapp.notifications.domain.model.notification.NotificationPreference;

import java.util.List;
import java.util.Optional;

public interface NotificationPreferenceRepository {
    Optional<NotificationPreference> findByUserIdAndCategory(Long userId, NotificationCategory category);

    List<NotificationPreference> findByUserId(Long userId);

    NotificationPreference save(NotificationPreference preference);
}
