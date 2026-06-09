package com.financialapp.notifications.domain.usecase.event.command;

import com.financialapp.notifications.domain.event.CardExpiring;

public record ProcessCardExpiringCommand(CardExpiring cardExpiring) {}
