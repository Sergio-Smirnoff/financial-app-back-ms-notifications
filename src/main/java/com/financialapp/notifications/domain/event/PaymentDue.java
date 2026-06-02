package com.financialapp.notifications.domain.event;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentDue(
        Long userId,
        Long cardExpenseId,
        String description,
        LocalDate nextDueDate,
        BigDecimal installmentAmount,
        String currency,
        int remainingInstallments
) {}
