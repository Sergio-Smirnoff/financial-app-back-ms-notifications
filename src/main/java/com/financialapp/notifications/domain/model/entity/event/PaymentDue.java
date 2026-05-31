package com.financialapp.notifications.domain.model.entity.event;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record PaymentDue(
        Long userId,
        Long cardExpenseId,
        String description,
        LocalDate nextDueDate,
        BigDecimal installmentAmount,
        String currency,
        int remainingInstallments
) {}
