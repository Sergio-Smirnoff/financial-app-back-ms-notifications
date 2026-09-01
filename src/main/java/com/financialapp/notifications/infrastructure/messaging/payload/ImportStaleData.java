package com.financialapp.notifications.infrastructure.messaging.payload;

public record ImportStaleData(
        Long userId,
        String accountCbu,
        String bankNumber,
        int daysSinceImport
) {}
