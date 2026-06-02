package com.financialapp.notifications.infrastructure.client;

import com.financialapp.notifications.domain.gateway.FinancesGateway;
import com.financialapp.notifications.domain.model.entity.summary.CategorySummary;
import com.financialapp.notifications.support.WireMockIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exercises the REAL FinancesClient (WebClient) against a WireMock stub server, covering the
 * mapped-success path (CategorySummaryMapper) and the error → empty-list catch branch.
 */
class FinancesClientWireMockIT extends WireMockIntegrationTest {

    @Autowired FinancesGateway financesGateway;

    @Test
    void getSummaryByCategory_mapsStubbedResponse_andSendsUserHeader() {
        // Given the finances service returns the canned summary (mappings/finances-summary.json)
        // When the gateway is called
        List<CategorySummary> result = financesGateway.getSummaryByCategory(7L, "2026-01-01", "2026-01-31");

        // Then the response is mapped through the real WebClient + CategorySummaryMapper
        assertThat(result).hasSize(1);
        assertThat(result.get(0).categoryName()).isEqualTo("Food");
        assertThat(result.get(0).totalAmount()).isEqualByComparingTo("1500.00");

        // And the outgoing request carried the X-User-Id header
        wireMock.verify(getRequestedFor(urlPathEqualTo("/api/v1/finances/transactions/summary-by-category"))
                .withHeader("X-User-Id", equalTo("7")));
    }

    @Test
    void getSummaryByCategory_returnsEmptyListOnDownstreamError() {
        // Given the finances service returns a 500 (programmatic, higher priority)
        wireMock.stubFor(get(urlPathEqualTo("/api/v1/finances/transactions/summary-by-category"))
                .atPriority(1)
                .willReturn(aResponse().withStatus(500).withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"boom\"}")));

        // When the gateway is called / Then the catch branch returns an empty list
        List<CategorySummary> result = financesGateway.getSummaryByCategory(7L, "2026-01-01", "2026-01-31");
        assertThat(result).isEmpty();
    }
}
