package com.financialapp.notifications.domain.usecase;

import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.pagination.PageResult;

public interface GetNotificationUseCase {
    PageResult<Notification> execute(Long userId, int page, int size);
}
