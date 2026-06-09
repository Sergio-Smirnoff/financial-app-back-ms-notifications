package com.financialapp.notifications.domain.event;

import java.math.BigDecimal;

public record BalanceAdjusted(
        Long userId,
        String accountName,
        String accountCbu,
        String bankNumber,
        BigDecimal amount,
        String currency,
        boolean credit
) {}
