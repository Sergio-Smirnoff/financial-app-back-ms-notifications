package com.financialapp.notifications.domain.usecase.event;

import com.financialapp.notifications.domain.usecase.event.command.ProcessImportStaleCommand;

public interface ProcessImportStaleUseCase {
    void execute(ProcessImportStaleCommand command);
}
