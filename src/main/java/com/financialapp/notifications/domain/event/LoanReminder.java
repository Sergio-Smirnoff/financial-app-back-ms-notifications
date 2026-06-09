package com.financialapp.notifications.domain.event;

import java.time.LocalDate;

public record LoanReminder(
        Long userId,
        Long loanId,
        Long installmentId,
        String loanName,
        int installmentNumber,
        LocalDate dueDate
) {}
