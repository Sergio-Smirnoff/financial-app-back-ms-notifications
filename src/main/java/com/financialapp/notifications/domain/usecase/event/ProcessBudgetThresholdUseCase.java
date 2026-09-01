package com.financialapp.notifications.domain.usecase.event;

import com.financialapp.notifications.domain.usecase.event.command.ProcessBudgetThresholdCommand;

public interface ProcessBudgetThresholdUseCase {
    void execute(ProcessBudgetThresholdCommand command);
}
