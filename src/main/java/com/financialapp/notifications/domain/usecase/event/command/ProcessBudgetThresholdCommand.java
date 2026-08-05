package com.financialapp.notifications.domain.usecase.event.command;

import com.financialapp.notifications.domain.event.BudgetThresholdReached;

public record ProcessBudgetThresholdCommand(BudgetThresholdReached budgetThreshold) {}
