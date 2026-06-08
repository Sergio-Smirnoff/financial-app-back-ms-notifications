package com.financialapp.notifications.domain.usecase.event;

import com.financialapp.notifications.domain.usecase.event.command.ProcessCardExpiringCommand;

public interface ProcessCardExpiringUseCase {
    void execute(ProcessCardExpiringCommand command);
}
