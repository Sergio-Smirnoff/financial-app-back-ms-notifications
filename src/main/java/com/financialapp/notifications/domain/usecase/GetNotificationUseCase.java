package com.financialapp.notifications.domain.usecase;

import com.financialapp.notifications.domain.model.response.NotificationResponse;
import com.financialapp.notifications.domain.model.response.PageResult;

public interface GetNotificationUseCase {
    PageResult<NotificationResponse> execute(Long userId, int page, int size);
}
