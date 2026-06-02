package com.financialapp.notifications.domain.model.entity.summary;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CategorySummaryTest {

    @Test
    void roundTripsAndCompares() {
        // Given a built CategorySummary
        CategorySummary a = CategorySummary.builder().categoryName("Food").subcategoryName("Cafe")
                .totalAmount(new BigDecimal("12.34")).currency("ARS").transactionCount(3L).build();
        CategorySummary b = CategorySummary.builder().categoryName("Food").subcategoryName("Cafe")
                .totalAmount(new BigDecimal("12.34")).currency("ARS").transactionCount(3L).build();

        // Then accessors round-trip and equality/toString behave as a record
        assertThat(a.categoryName()).isEqualTo("Food");
        assertThat(a.subcategoryName()).isEqualTo("Cafe");
        assertThat(a.totalAmount()).isEqualByComparingTo("12.34");
        assertThat(a.currency()).isEqualTo("ARS");
        assertThat(a.transactionCount()).isEqualTo(3L);
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a).isNotEqualTo(CategorySummary.builder().categoryName("Other").build());
        assertThat(a.toString()).contains("CategorySummary");
    }
}
