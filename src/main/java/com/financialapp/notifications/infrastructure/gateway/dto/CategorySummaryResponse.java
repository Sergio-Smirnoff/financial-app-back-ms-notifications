package com.financialapp.notifications.infrastructure.gateway.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategorySummaryResponse {
    private Long categoryId;
    private String categoryName;
    private String total;
    private String currency;
    private Long transactionCount;
}
