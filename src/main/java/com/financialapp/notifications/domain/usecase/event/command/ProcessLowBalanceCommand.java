package com.financialapp.notifications.domain.usecase.event.command;

import com.financialapp.notifications.domain.event.LowBalance;

public record ProcessLowBalanceCommand(LowBalance lowBalance) {}
