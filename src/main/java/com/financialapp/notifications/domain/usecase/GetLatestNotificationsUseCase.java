package com.financialapp.notifications.domain.usecase;

import java.util.List;

import com.financialapp.notifications.domain.model.response.NotificationResponse;

public interface GetLatestNotificationsUseCase {
    List<NotificationResponse> execute(Long userId, Long bankId);
}
