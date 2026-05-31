package com.financialapp.notifications.infrastructure.kafka.mapper;

import com.financialapp.notifications.infrastructure.kafka.event.LoanReminderEvent;
import com.financialapp.notifications.domain.model.entity.event.LoanReminder;

public class LoanReminderMapper {
    public static LoanReminder toDomain(LoanReminderEvent event) {
        LoanReminderEvent.Payload p = event.getPayload();
        return LoanReminder.builder()
                .userId(event.getUserId())
                .loanId(p.getLoanId())
                .loanDescription(p.getLoanDescription())
                .nextPaymentDate(p.getNextPaymentDate())
                .installmentAmount(p.getInstallmentAmount())
                .currency(p.getCurrency())
                .remainingInstallments(p.getRemainingInstallments())
                .build();
    }
}
