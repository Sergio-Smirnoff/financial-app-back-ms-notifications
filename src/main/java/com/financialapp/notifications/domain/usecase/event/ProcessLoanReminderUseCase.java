package com.financialapp.notifications.domain.usecase.event;

import com.financialapp.notifications.domain.event.LoanReminder;

public interface ProcessLoanReminderUseCase {
    void execute(LoanReminder reminder);
}
