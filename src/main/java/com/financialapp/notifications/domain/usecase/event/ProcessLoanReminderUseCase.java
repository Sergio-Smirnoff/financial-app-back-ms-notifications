package com.financialapp.notifications.domain.usecase.event;

import com.financialapp.notifications.domain.usecase.event.command.ProcessLoanReminderCommand;

public interface ProcessLoanReminderUseCase {
    void execute(ProcessLoanReminderCommand command);
}
