package com.financialapp.notifications.domain.usecase.event.command;

import com.financialapp.notifications.domain.event.InvestmentThreshold;

public record ProcessInvestmentThresholdCommand(InvestmentThreshold threshold) {
}
