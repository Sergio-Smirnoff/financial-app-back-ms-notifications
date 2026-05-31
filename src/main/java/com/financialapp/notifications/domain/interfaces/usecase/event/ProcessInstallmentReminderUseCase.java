package com.financialapp.notifications.domain.interfaces.usecase.event;

import com.financialapp.notifications.domain.model.entity.event.InstallmentReminder;

public interface ProcessInstallmentReminderUseCase {
    public void execute(InstallmentReminder reminder);
}
