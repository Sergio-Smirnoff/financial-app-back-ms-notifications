package com.financialapp.notifications.domain.event;

import java.math.BigDecimal;
import java.time.LocalDate;

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
