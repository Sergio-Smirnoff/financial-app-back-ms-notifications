package com.financialapp.notifications.infrastructure.messaging.mapper;

import com.financialapp.notifications.domain.event.LoanReminder;
import com.financialapp.notifications.infrastructure.messaging.payload.LoanReminderData;

public class LoanReminderMapper {

    public static LoanReminder toDomain(LoanReminderData data) {
        return new LoanReminder(
                data.userId(),
                data.loanId(),
                data.installmentId(),
                data.loanName(),
                data.installmentNumber(),
                data.dueDate()
        );
    }
}
