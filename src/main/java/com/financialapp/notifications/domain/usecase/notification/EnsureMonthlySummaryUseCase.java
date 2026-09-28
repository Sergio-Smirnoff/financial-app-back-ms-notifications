package com.financialapp.notifications.domain.usecase.notification;

import com.financialapp.notifications.domain.usecase.notification.command.EnsureMonthlySummaryCommand;

public interface EnsureMonthlySummaryUseCase {

    MonthlySummaryResult execute(EnsureMonthlySummaryCommand command);
}
