package com.financialapp.notifications.domain.usecase.event;

import com.financialapp.notifications.domain.usecase.event.command.ProcessBankEventCommand;

public interface ProcessBankEventUseCase {
    void execute(ProcessBankEventCommand command);
}
