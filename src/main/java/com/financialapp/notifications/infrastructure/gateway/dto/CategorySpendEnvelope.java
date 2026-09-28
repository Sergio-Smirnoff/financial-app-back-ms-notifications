package com.financialapp.notifications.infrastructure.gateway.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CategorySpendEnvelope(List<CategorySummaryResponse> data) {}
