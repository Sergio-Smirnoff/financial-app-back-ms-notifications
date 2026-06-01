package com.financialapp.notifications.domain.usecase.event;

import com.financialapp.notifications.domain.model.entity.event.PaymentDue;

public interface ProcessPaymentDueUseCase {
    void execute(PaymentDue paymentDue);
}
