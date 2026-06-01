package com.financialapp.notifications.domain.usecase;

import com.financialapp.notifications.domain.model.response.NotificationPreferenceResponse;

public interface GetPreferenceUseCase {
    NotificationPreferenceResponse execute(Long userId);
}