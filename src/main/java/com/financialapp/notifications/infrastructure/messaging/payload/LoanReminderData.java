package com.financialapp.notifications.infrastructure.messaging.payload;

import java.time.LocalDate;

public record LoanReminderData(
        Long userId,
        Long loanId,
        Long installmentId,
        int installmentNumber,
        String loanName,
        LocalDate dueDate
) {}
