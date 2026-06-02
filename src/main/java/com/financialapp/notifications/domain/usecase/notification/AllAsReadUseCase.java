package com.financialapp.notifications.domain.usecase.notification;

import com.financialapp.notifications.domain.usecase.notification.command.AllAsReadCommand;

public interface AllAsReadUseCase {
    public void execute(AllAsReadCommand command);
}
