package com.financialapp.notifications.domain.usecase.event;

import com.financialapp.notifications.domain.usecase.event.command.ProcessPaymentDueCommand;

public interface ProcessPaymentDueUseCase {
    void execute(ProcessPaymentDueCommand command);
}
