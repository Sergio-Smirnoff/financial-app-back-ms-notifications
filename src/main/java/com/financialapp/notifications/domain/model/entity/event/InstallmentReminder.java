package com.financialapp.notifications.domain.model.entity.event;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record InstallmentReminder(
        Long userId,
        Long loanId,
        Long installmentId,
        String loanDescription,
        int installmentNumber,
        LocalDate dueDate,
        BigDecimal amount,
        String currency
) {}