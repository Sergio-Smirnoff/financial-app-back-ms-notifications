package com.financialapp.notifications.domain.model.entity.event;

import java.math.BigDecimal;

public record InvestmentThreshold(
        Long userId,
        Long holdingId,
        String ticker,
        String name,
        String direction,
        BigDecimal thresholdPct,
        BigDecimal actualPct,
        BigDecimal currentPrice,
        BigDecimal avgPurchasePrice,
        String currency
) {}
