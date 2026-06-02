package com.financialapp.notifications.infrastructure.gateway.mapper;

import com.financialapp.notifications.domain.model.category.CategorySummary;
import com.financialapp.notifications.infrastructure.gateway.dto.CategorySummaryResponse;

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
