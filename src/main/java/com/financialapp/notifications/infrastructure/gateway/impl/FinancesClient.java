package com.financialapp.notifications.infrastructure.gateway.impl;

import com.financialapp.notifications.domain.gateway.FinancesGateway;
import com.financialapp.notifications.domain.model.category.CategorySummary;
import com.financialapp.notifications.infrastructure.gateway.dto.CategorySpendEnvelope;
import com.financialapp.notifications.infrastructure.gateway.mapper.CategorySummaryMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
public class FinancesClient implements FinancesGateway {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private final WebClient.Builder webClientBuilder;
    private final String financesServiceUrl;
    private final String internalToken;

    public FinancesClient(WebClient.Builder webClientBuilder,
                          @Value("${finances.service.url}") String financesServiceUrl,
                          @Value("${internal.auth.token}") String internalToken) {
        this.webClientBuilder = webClientBuilder;
        this.financesServiceUrl = financesServiceUrl;
        this.internalToken = internalToken;
    }

    @Override
    public List<CategorySummary> getSummaryByCategory(Long userId, String dateFrom, String dateTo) {
        CategorySpendEnvelope envelope = webClientBuilder.build()
                .get()
                .uri(financesServiceUrl + "/api/v1/finances/categories/spend?from={from}&to={to}&kind=EXPENSE",
                        dateFrom, dateTo)
                .header("X-User-Id", String.valueOf(userId))
                .header("X-Internal-Token", internalToken)
                .retrieve()
                .bodyToMono(CategorySpendEnvelope.class)
                .block(TIMEOUT);
        return Optional.ofNullable(envelope)
                .map(CategorySpendEnvelope::data)
                .orElseThrow(() -> new IllegalStateException("ms-finances: category spend answered without data"))
                .stream()
                .map(CategorySummaryMapper::toDomain)
                .toList();
    }
}
