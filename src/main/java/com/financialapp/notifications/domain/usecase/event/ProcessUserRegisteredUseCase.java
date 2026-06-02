package com.financialapp.notifications.domain.usecase.event;

import com.financialapp.notifications.domain.usecase.event.command.ProcessUserRegisteredCommand;

public interface ProcessUserRegisteredUseCase {
    void execute(ProcessUserRegisteredCommand command);
}
