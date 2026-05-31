package com.financialapp.notifications.domain.gateway;


import com.financialapp.notifications.domain.model.entity.summary.CategorySummary;

import java.util.List;

public interface FinancesGateway {
    List<CategorySummary> getSummaryByCategory(Long userId, String dateFrom, String dateTo);
}
