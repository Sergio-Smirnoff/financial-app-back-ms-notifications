package com.financialapp.notifications.infrastructure.messaging.mapper;

import com.financialapp.notifications.domain.event.InvestmentThreshold;
import com.financialapp.notifications.infrastructure.messaging.payload.InvestmentThresholdData;

public class InvestmentThresholdMapper {

    public static InvestmentThreshold toDomain(InvestmentThresholdData data) {
        return new InvestmentThreshold(
                data.userId(),
                data.holdingId(),
                data.ticker(),
                data.name(),
                data.direction(),
                data.thresholdPct(),
                data.actualPct(),
                data.currentPrice(),
                data.avgPurchasePrice(),
                data.currency()
        );
    }
}
