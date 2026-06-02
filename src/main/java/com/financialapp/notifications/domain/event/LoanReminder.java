package com.financialapp.notifications.domain.event;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoanReminder(
        Long userId,
        Long loanId,
        String loanDescription,
        LocalDate nextPaymentDate,
        BigDecimal installmentAmount,
        String currency,
        int remainingInstallments
) {}
