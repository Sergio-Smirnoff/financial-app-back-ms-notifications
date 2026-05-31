package com.financialapp.notifications.domain.model.entity.summary;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CategorySummary(
        String categoryName,
        String subcategoryName,
        BigDecimal totalAmount,
        String currency,
        Long transactionCount
) {}
