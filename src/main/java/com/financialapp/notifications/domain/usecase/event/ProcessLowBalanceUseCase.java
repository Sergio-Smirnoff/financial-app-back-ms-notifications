package com.financialapp.notifications.domain.usecase.event;

import com.financialapp.notifications.domain.usecase.event.command.ProcessLowBalanceCommand;

public interface ProcessLowBalanceUseCase {
    void execute(ProcessLowBalanceCommand command);
}
