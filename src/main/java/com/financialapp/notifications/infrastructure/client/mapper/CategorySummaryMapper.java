package com.financialapp.notifications.infrastructure.client.mapper;

import com.financialapp.notifications.domain.model.entity.summary.CategorySummary;
import com.financialapp.notifications.infrastructure.client.dto.CategorySummaryResponse;

public class CategorySummaryMapper {
    public static CategorySummary toDomain(CategorySummaryResponse response) {
        return new CategorySummary(
                response.getCategoryName(),
                response.getSubcategoryName(),
                response.getTotalAmount(),
                response.getCurrency(),
                response.getTransactionCount()
        );
    }
}
