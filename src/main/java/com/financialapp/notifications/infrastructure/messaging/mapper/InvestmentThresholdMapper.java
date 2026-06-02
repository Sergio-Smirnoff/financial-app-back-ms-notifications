package com.financialapp.notifications.infrastructure.messaging.mapper;

import com.financialapp.notifications.infrastructure.messaging.payload.InvestmentThresholdEvent;
import com.financialapp.notifications.domain.event.InvestmentThreshold;

public class InvestmentThresholdMapper {
    public static InvestmentThreshold toDomain(InvestmentThresholdEvent event) {
        InvestmentThresholdEvent.Payload p = event.getPayload();
        return new InvestmentThreshold(
                event.getUserId(),
                p.getHoldingId(),
                p.getTicker(),
                p.getName(),
                p.getDirection(),
                p.getThresholdPct(),
                p.getActualPct(),
                p.getCurrentPrice(),
                p.getAvgPurchasePrice(),
                p.getCurrency()
        );
    }
}
