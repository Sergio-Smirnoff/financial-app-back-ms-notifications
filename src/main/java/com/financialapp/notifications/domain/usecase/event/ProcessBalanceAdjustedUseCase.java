package com.financialapp.notifications.domain.usecase.event;

import com.financialapp.notifications.domain.usecase.event.command.ProcessBalanceAdjustedCommand;

public interface ProcessBalanceAdjustedUseCase {
    void execute(ProcessBalanceAdjustedCommand command);
}
