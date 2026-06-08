package com.financialapp.notifications.infrastructure.messaging.mapper;

import com.financialapp.notifications.domain.event.PaymentDue;
import com.financialapp.notifications.infrastructure.messaging.payload.CardInstallmentDueData;

public class PaymentDueMapper {

    public static PaymentDue toDomain(CardInstallmentDueData data) {
        return new PaymentDue(
                data.userId(),
                data.cardNumber(),
                data.installmentId(),
                data.installmentNumber(),
                data.totalInstallments(),
                data.description(),
                data.dueDate(),
                data.amount(),
                data.currency()
        );
    }
}
