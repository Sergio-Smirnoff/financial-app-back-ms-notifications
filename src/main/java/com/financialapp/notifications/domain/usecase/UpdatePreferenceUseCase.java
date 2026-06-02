package com.financialapp.notifications.domain.usecase;

import com.financialapp.notifications.domain.model.entity.UserNotificationPreference;

public interface UpdatePreferenceUseCase {
    UserNotificationPreference execute(Long userId, boolean monthlyEmailEnabled);
}