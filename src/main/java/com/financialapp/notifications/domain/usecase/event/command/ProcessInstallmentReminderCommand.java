package com.financialapp.notifications.domain.usecase.event.command;

import com.financialapp.notifications.domain.event.InstallmentReminder;

public record ProcessInstallmentReminderCommand(InstallmentReminder reminder) {
}
