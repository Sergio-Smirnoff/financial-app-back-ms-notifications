package com.financialapp.notifications.domain.usecase.event.command;

import com.financialapp.notifications.domain.event.LoanReminder;

public record ProcessLoanReminderCommand(LoanReminder reminder) {
}
