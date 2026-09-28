package com.financialapp.notifications.infrastructure.client;

import com.financialapp.notifications.domain.gateway.FinancesGateway;
import com.financialapp.notifications.domain.model.category.CategorySummary;
import com.financialapp.notifications.support.WireMockIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FinancesClientWireMockIT extends WireMockIntegrationTest {

    private static final String SPEND = "/api/v1/finances/categories/spend";

    @Autowired private FinancesGateway financesGateway;

    @Test
    void getSummaryByCategory_readsTheSpendEnvelope_andSendsTheUserAndTheInternalToken() {
        List<CategorySummary> result = financesGateway.getSummaryByCategory(7L, "2026-08-01", "2026-08-31");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).categoryName()).isEqualTo("Food");
        assertThat(result.get(0).totalAmount()).isEqualByComparingTo("1500.00");
        assertThat(result.get(0).transactionCount()).isEqualTo(3L);
        wireMock.verify(getRequestedFor(urlPathEqualTo(SPEND))
                .withQueryParam("from", equalTo("2026-08-01"))
                .withQueryParam("to", equalTo("2026-08-31"))
                .withQueryParam("kind", equalTo("EXPENSE"))
                .withHeader("X-User-Id", equalTo("7"))
                .withHeader("X-Internal-Token", equalTo("test-token")));
    }

    @Test
    void getSummaryByCategory_downstreamError_propagates() {
        wireMock.stubFor(get(urlPathEqualTo(SPEND)).atPriority(1)
                .willReturn(aResponse().withStatus(500).withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":500}")));

        assertThatThrownBy(() -> financesGateway.getSummaryByCategory(7L, "2026-08-01", "2026-08-31"))
                .isInstanceOf(WebClientResponseException.class);
    }

    @Test
    void getSummaryByCategory_answerWithoutData_propagates() {
        wireMock.stubFor(get(urlPathEqualTo(SPEND)).atPriority(1)
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":200,\"title\":\"OK\"}")));

        assertThatThrownBy(() -> financesGateway.getSummaryByCategory(7L, "2026-08-01", "2026-08-31"))
                .isInstanceOf(IllegalStateException.class);
    }
}
