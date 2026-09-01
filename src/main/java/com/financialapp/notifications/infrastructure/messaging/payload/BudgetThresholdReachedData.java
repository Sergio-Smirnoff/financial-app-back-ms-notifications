package com.financialapp.notifications.infrastructure.messaging.payload;

import java.math.BigDecimal;

public record BudgetThresholdReachedData(
        Long budgetId,
        Long userId,
        Long categoryId,
        BigDecimal pctUsed,
        BigDecimal alertThresholdPct,
        Integer year,
        Integer month
) {}
