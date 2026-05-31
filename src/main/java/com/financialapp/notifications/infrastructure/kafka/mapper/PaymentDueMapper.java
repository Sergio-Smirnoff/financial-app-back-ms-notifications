package com.financialapp.notifications.infrastructure.kafka.mapper;

import com.financialapp.notifications.infrastructure.kafka.event.PaymentDueEvent;
import com.financialapp.notifications.domain.model.entity.event.PaymentDue;

public class PaymentDueMapper {
    public static PaymentDue toDomain(PaymentDueEvent event) {
        PaymentDueEvent.Payload p = event.getPayload();
        return PaymentDue.builder()
                .userId(event.getUserId())
                .cardExpenseId(p.getCardExpenseId())
                .description(p.getDescription())
                .nextDueDate(p.getNextDueDate())
                .installmentAmount(p.getInstallmentAmount())
                .currency(p.getCurrency())
                .remainingInstallments(p.getRemainingInstallments())
                .build();
    }
}
