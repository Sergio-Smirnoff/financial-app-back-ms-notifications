package com.financialapp.notifications.domain.event;

import java.util.Objects;

public record ImportStale(
        Long userId,
        String accountCbu,
        String bankNumber,
        int daysSinceImport
) {
    public ImportStale {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(accountCbu, "accountCbu must not be null");
        if (daysSinceImport < 0) {
            throw new IllegalArgumentException("daysSinceImport must not be negative");
        }
    }
}
