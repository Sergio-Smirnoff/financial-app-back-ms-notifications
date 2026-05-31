package com.financialapp.notifications.infrastructure.kafka.mapper;

import com.financialapp.notifications.infrastructure.kafka.event.InvestmentThresholdEvent;
import com.financialapp.notifications.domain.model.entity.event.InvestmentThreshold;

public class InvestmentThresholdMapper {
    public static InvestmentThreshold toDomain(InvestmentThresholdEvent event) {
        InvestmentThresholdEvent.Payload p = event.getPayload();
        return InvestmentThreshold.builder()
                .userId(event.getUserId())
                .holdingId(p.getHoldingId())
                .ticker(p.getTicker())
                .name(p.getName())
                .direction(p.getDirection())
                .thresholdPct(p.getThresholdPct())
                .actualPct(p.getActualPct())
                .currentPrice(p.getCurrentPrice())
                .avgPurchasePrice(p.getAvgPurchasePrice())
                .currency(p.getCurrency())
                .build();
    }
}
