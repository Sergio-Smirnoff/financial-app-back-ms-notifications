package com.financialapp.notifications.domain.usecase.event.command;

import com.financialapp.notifications.domain.event.BankAlert;

public record ProcessBankEventCommand(BankAlert alert) {
}
