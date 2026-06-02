package com.financialapp.notifications.domain.usecase.event;

import com.financialapp.notifications.domain.event.InvestmentThreshold;

public interface ProcessInvestmentThresholdUseCase {
    void execute(InvestmentThreshold threshold);
}
