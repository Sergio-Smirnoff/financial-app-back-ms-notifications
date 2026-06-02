package com.financialapp.notifications.domain.model.category;

import java.math.BigDecimal;

public record CategorySummary(
        String categoryName,
        String subcategoryName,
        BigDecimal totalAmount,
        String currency,
        Long transactionCount
) {}
