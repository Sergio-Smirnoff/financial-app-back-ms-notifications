package com.financialapp.notifications.domain.interfaces.usecase.event;

import com.financialapp.notifications.domain.model.entity.event.InvestmentThreshold;

public interface ProcessInvestmentThresholdUseCase {
    void execute(InvestmentThreshold threshold);
}
