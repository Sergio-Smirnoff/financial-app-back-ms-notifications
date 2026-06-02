package com.financialapp.notifications.infrastructure.kafka.mapper;

import com.financialapp.notifications.infrastructure.kafka.event.PaymentDueEvent;
import com.financialapp.notifications.domain.event.PaymentDue;

public class PaymentDueMapper {
    public static PaymentDue toDomain(PaymentDueEvent event) {
        PaymentDueEvent.Payload p = event.getPayload();
        return new PaymentDue(
                event.getUserId(),
                p.getCardExpenseId(),
                p.getDescription(),
                p.getNextDueDate(),
                p.getInstallmentAmount(),
                p.getCurrency(),
                p.getRemainingInstallments()
        );
    }
}
