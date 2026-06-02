package com.financialapp.notifications.domain.usecase.notification;

import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.pagination.PageResult;
import com.financialapp.notifications.domain.usecase.notification.command.GetNotificationsCommand;

public interface GetNotificationUseCase {
    PageResult<Notification> execute(GetNotificationsCommand command);
}
