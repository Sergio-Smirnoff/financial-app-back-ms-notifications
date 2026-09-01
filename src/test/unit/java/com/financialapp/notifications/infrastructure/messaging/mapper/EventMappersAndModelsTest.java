package com.financialapp.notifications.infrastructure.messaging.mapper;

import com.financialapp.notifications.domain.event.BudgetThresholdReached;
import com.financialapp.notifications.domain.event.ImportStale;
import com.financialapp.notifications.infrastructure.messaging.payload.BudgetThresholdReachedData;
import com.financialapp.notifications.infrastructure.messaging.payload.ImportStaleData;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventMappersAndModelsTest {

    @Test
    void mapsBudgetThresholdReachedDataToDomain() {
        BudgetThresholdReachedData data = new BudgetThresholdReachedData(
                100L, 42L, 5L, new BigDecimal("85.5"), new BigDecimal("80.0"), 2026, 8);

        BudgetThresholdReached domain = BudgetThresholdMapper.toDomain(data);

        assertThat(domain.budgetId()).isEqualTo(100L);
        assertThat(domain.userId()).isEqualTo(42L);
        assertThat(domain.categoryId()).isEqualTo(5L);
        assertThat(domain.pctUsed()).isEqualByComparingTo("85.5");
        assertThat(domain.alertThresholdPct()).isEqualByComparingTo("80.0");
        assertThat(domain.year()).isEqualTo(2026);
        assertThat(domain.month()).isEqualTo(8);
    }

    @Test
    void mapsImportStaleDataToDomain() {
        ImportStaleData data = new ImportStaleData(42L, "0170099220000067797370", "017", 35);

        ImportStale domain = ImportStaleMapper.toDomain(data);

        assertThat(domain.userId()).isEqualTo(42L);
        assertThat(domain.accountCbu()).isEqualTo("0170099220000067797370");
        assertThat(domain.bankNumber()).isEqualTo("017");
        assertThat(domain.daysSinceImport()).isEqualTo(35);
    }

    @Test
    void rejectsNegativeDaysSinceImportInImportStale() {
        assertThatThrownBy(() -> new ImportStale(42L, "0170099220000067797370", "017", -1))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
