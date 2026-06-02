package com.financialapp.notifications.domain.usecase;

import com.financialapp.notifications.domain.model.entity.UserNotificationPreference;

public interface GetPreferenceUseCase {
    UserNotificationPreference execute(Long userId);
}