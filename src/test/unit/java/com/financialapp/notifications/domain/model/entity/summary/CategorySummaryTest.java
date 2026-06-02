package com.financialapp.notifications.domain.model.entity.summary;

import com.financialapp.notifications.domain.model.category.CategorySummary;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CategorySummaryTest {

    @Test
    void roundTripsAndCompares() {
        // Given a built CategorySummary
        CategorySummary a = new CategorySummary("Food", "Cafe", new BigDecimal("12.34"), "ARS", 3L);
        CategorySummary b = new CategorySummary("Food", "Cafe", new BigDecimal("12.34"), "ARS", 3L);

        // Then accessors round-trip and equality/toString behave as a record
        assertThat(a.categoryName()).isEqualTo("Food");
        assertThat(a.subcategoryName()).isEqualTo("Cafe");
        assertThat(a.totalAmount()).isEqualByComparingTo("12.34");
        assertThat(a.currency()).isEqualTo("ARS");
        assertThat(a.transactionCount()).isEqualTo(3L);
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a).isNotEqualTo(new CategorySummary("Other", null, null, null, null));
        assertThat(a.toString()).contains("CategorySummary");
    }
}
