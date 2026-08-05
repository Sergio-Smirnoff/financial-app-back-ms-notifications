package com.financialapp.notifications.infrastructure.messaging.mapper;

import com.financialapp.notifications.domain.event.BudgetThresholdReached;
import com.financialapp.notifications.infrastructure.messaging.payload.BudgetThresholdReachedData;

public final class BudgetThresholdMapper {

    private BudgetThresholdMapper() {}

    public static BudgetThresholdReached toDomain(BudgetThresholdReachedData data) {
        return new BudgetThresholdReached(
                data.budgetId(),
                data.userId(),
                data.categoryId(),
                data.pctUsed(),
                data.alertThresholdPct(),
                data.year(),
                data.month()
        );
    }
}
