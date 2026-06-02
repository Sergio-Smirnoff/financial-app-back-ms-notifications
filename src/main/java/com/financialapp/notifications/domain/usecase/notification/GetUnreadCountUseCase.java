package com.financialapp.notifications.domain.usecase.notification;

import com.financialapp.notifications.domain.usecase.notification.command.GetUnreadCountCommand;

public interface GetUnreadCountUseCase {
    long execute(GetUnreadCountCommand command);
}
