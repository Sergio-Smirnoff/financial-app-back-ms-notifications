package com.financialapp.notifications.infrastructure.client.mapper;

import com.financialapp.notifications.domain.model.entity.summary.CategorySummary;
import com.financialapp.notifications.infrastructure.client.dto.CategorySummaryResponse;

public class CategorySummaryMapper {
    public static CategorySummary toDomain(CategorySummaryResponse response) {
        return CategorySummary.builder()
                .categoryName(response.getCategoryName())
                .subcategoryName(response.getSubcategoryName())
                .totalAmount(response.getTotalAmount())
                .currency(response.getCurrency())
                .transactionCount(response.getTransactionCount())
                .build();
    }
}
