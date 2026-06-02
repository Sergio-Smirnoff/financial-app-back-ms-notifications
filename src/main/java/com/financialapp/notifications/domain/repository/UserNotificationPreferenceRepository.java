package com.financialapp.notifications.domain.repository;

import com.financialapp.notifications.domain.model.entity.UserNotificationPreference;
import com.financialapp.notifications.domain.model.pagination.PageResult;

import java.util.Optional;

public interface UserNotificationPreferenceRepository {
    Optional<UserNotificationPreference> findByUserId(Long userId);

    PageResult<UserNotificationPreference> findByMonthlyEmailEnabledTrue(int page, int size);

    UserNotificationPreference save(UserNotificationPreference preference);

}
