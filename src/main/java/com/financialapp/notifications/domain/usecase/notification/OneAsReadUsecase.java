package com.financialapp.notifications.domain.usecase.notification;

import com.financialapp.notifications.domain.usecase.notification.command.MarkOneAsReadCommand;

public interface OneAsReadUsecase {
    void execute(MarkOneAsReadCommand command);
}
