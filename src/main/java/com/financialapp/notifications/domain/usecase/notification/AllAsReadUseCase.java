package com.financialapp.notifications.domain.usecase.notification;

import com.financialapp.notifications.domain.usecase.notification.command.AllAsReadCommand;

public interface AllAsReadUseCase {
    void execute(AllAsReadCommand command);
}
