package com.financialapp.notifications.domain.usecase.notification;

import java.time.YearMonth;

public record MonthlySummaryResult(boolean sent, YearMonth month, MonthlySummarySkip skip) {}
