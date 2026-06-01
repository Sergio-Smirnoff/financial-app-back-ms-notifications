package com.financialapp.notifications.domain.usecase;

import com.financialapp.notifications.domain.model.response.NotificationResponse;

public interface GetNotificationUseCase {
    public Page<NotificationResponse> execute(Long userId, Pageable pageable);
}
