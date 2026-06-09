package com.financialapp.notifications.domain.event;

import java.math.BigDecimal;

public record LowBalance(
        Long userId,
        String accountName,
        String accountCbu,
        String bankNumber,
        BigDecimal balance,
        String currency
) {}
