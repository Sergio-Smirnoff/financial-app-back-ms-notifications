package com.financialapp.notifications.domain.usecase.event;

import com.financialapp.notifications.domain.event.PaymentDue;

public interface ProcessPaymentDueUseCase {
    void execute(PaymentDue paymentDue);
}
