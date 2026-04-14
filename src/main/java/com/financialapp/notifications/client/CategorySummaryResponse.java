package com.financialapp.notifications.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategorySummaryResponse {
    private String categoryName;
    private String subcategoryName;
    private BigDecimal totalAmount;
    private String currency;
    private Long transactionCount;
}
