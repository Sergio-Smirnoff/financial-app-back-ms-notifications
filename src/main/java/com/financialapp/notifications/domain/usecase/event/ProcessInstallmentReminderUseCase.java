package com.financialapp.notifications.domain.usecase.event;

import com.financialapp.notifications.domain.event.InstallmentReminder;

public interface ProcessInstallmentReminderUseCase {
    public void execute(InstallmentReminder reminder);
}
