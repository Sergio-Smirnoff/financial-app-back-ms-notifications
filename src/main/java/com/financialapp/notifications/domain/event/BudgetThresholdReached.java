package com.financialapp.notifications.domain.event;

import java.math.BigDecimal;
import java.util.Objects;

public record BudgetThresholdReached(
        Long budgetId,
        Long userId,
        Long categoryId,
        BigDecimal pctUsed,
        BigDecimal alertThresholdPct,
        Integer year,
        Integer month
) {
    public BudgetThresholdReached {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(pctUsed, "pctUsed must not be null");
        Objects.requireNonNull(alertThresholdPct, "alertThresholdPct must not be null");
    }
}
