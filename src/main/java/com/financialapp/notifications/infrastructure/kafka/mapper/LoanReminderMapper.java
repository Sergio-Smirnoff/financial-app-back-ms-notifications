package com.financialapp.notifications.infrastructure.kafka.mapper;

import com.financialapp.notifications.infrastructure.kafka.event.LoanReminderEvent;
import com.financialapp.notifications.domain.event.LoanReminder;

public class LoanReminderMapper {
    public static LoanReminder toDomain(LoanReminderEvent event) {
        LoanReminderEvent.Payload p = event.getPayload();
        return new LoanReminder(
                event.getUserId(),
                p.getLoanId(),
                p.getLoanDescription(),
                p.getNextPaymentDate(),
                p.getInstallmentAmount(),
                p.getCurrency(),
                p.getRemainingInstallments()
        );
    }
}
