package com.financialapp.notifications.domain.event;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CardInstallmentDue(
        Long userId,
        String cardNumber,
        Long installmentId,
        int installmentNumber,
        int totalInstallments,
        String description,
        LocalDate dueDate,
        BigDecimal amount,
        String currency
) {}
