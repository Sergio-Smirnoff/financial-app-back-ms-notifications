package com.financialapp.notifications.infrastructure.messaging.mapper;

import com.financialapp.notifications.domain.event.ImportStale;
import com.financialapp.notifications.infrastructure.messaging.payload.ImportStaleData;

public final class ImportStaleMapper {

    private ImportStaleMapper() {}

    public static ImportStale toDomain(ImportStaleData data) {
        return new ImportStale(
                data.userId(),
                data.accountCbu(),
                data.bankNumber(),
                data.daysSinceImport()
        );
    }
}
