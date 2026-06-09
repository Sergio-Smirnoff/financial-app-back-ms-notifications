package com.financialapp.notifications.infrastructure.messaging.mapper;

import com.financialapp.notifications.domain.event.LowBalance;
import com.financialapp.notifications.infrastructure.messaging.payload.LowBalanceData;

public class LowBalanceMapper {

    public static LowBalance toDomain(LowBalanceData data) {
        return new LowBalance(
                data.userId(),
                data.accountName(),
                data.accountCbu(),
                data.bankNumber(),
                data.balance(),
                data.currency()
        );
    }
}
