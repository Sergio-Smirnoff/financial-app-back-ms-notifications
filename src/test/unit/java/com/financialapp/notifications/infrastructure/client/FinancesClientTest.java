package com.financialapp.notifications.infrastructure.client;

import com.financialapp.notifications.domain.model.category.CategorySummary;
import com.financialapp.notifications.infrastructure.gateway.impl.FinancesClient;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FinancesClientTest {

    @Test
    void getSummaryByCategory_returnsEmptyListWhenCallFails() {
        // Given a client pointed at an unroutable URL so the blocking call throws
        FinancesClient client = new FinancesClient(WebClient.builder());
        ReflectionTestUtils.setField(client, "financesServiceUrl", "http://localhost:1");

        // When fetching the summary
        List<CategorySummary> result = client.getSummaryByCategory(7L, "2026-01-01", "2026-01-31");

        // Then the failure is swallowed and an empty list is returned
        assertThat(result).isEmpty();
    }
}
