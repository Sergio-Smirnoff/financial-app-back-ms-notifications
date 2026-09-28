package com.financialapp.notifications.web.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MonthlySummaryResponse(boolean sent, String month, String reason) {}
