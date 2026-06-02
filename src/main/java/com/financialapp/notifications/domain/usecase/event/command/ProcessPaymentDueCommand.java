package com.financialapp.notifications.domain.usecase.event.command;

import com.financialapp.notifications.domain.event.PaymentDue;

public record ProcessPaymentDueCommand(PaymentDue paymentDue) {
}
