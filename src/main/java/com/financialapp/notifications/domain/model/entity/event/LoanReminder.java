package com.financialapp.notifications.domain.model.entity.event;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record LoanReminder(
        Long userId,
        Long loanId,
        String loanDescription,
        LocalDate nextPaymentDate,
        BigDecimal installmentAmount,
        String currency,
        int remainingInstallments
) {}
