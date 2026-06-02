package com.financialapp.notifications.domain.usecase.event;

import com.financialapp.notifications.domain.usecase.event.command.ProcessInvestmentThresholdCommand;

public interface ProcessInvestmentThresholdUseCase {
    void execute(ProcessInvestmentThresholdCommand command);
}
