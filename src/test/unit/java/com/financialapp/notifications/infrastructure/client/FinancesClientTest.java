package com.financialapp.notifications.infrastructure.client;

import com.financialapp.notifications.infrastructure.gateway.impl.FinancesClient;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FinancesClientTest {

    @Test
    void getSummaryByCategory_unreachableService_propagates() {
        FinancesClient client = new FinancesClient(WebClient.builder(), "http://localhost:1", "token");

        assertThatThrownBy(() -> client.getSummaryByCategory(7L, "2026-08-01", "2026-08-31"))
                .isInstanceOf(WebClientRequestException.class);
    }
}
