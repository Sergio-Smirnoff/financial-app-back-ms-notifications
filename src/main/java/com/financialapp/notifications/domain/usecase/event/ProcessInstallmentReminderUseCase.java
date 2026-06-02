package com.financialapp.notifications.domain.usecase.event;

import com.financialapp.notifications.domain.usecase.event.command.ProcessInstallmentReminderCommand;

public interface ProcessInstallmentReminderUseCase {
    public void execute(ProcessInstallmentReminderCommand command);
}
