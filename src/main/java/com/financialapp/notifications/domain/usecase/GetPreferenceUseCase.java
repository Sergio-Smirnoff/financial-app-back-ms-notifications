package com.financialapp.notifications.domain.usecase;

import com.financialapp.notifications.domain.model.notification.UserNotificationPreference;

public interface GetPreferenceUseCase {
    UserNotificationPreference execute(Long userId);
}