package com.financialapp.notifications.infrastructure.messaging.mapper;

import com.financialapp.notifications.domain.event.BalanceAdjusted;
import com.financialapp.notifications.infrastructure.messaging.payload.BalanceAdjustedData;

public class BalanceAdjustedMapper {

    public static BalanceAdjusted toDomain(BalanceAdjustedData data) {
        return new BalanceAdjusted(
                data.userId(),
                data.accountName(),
                data.accountCbu(),
                data.bankNumber(),
                data.amount(),
                data.currency(),
                data.credit()
        );
    }
}
