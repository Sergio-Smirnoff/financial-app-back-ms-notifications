package com.financialapp.notifications.domain.usecase;

import com.financialapp.notifications.domain.model.response.NotificationPreferenceResponse;

public interface UpdatePreferenceUseCase {
    NotificationPreferenceResponse execute(Long userId, boolean monthlyEmailEnabled);
}