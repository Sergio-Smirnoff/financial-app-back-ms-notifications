package com.financialapp.notifications.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinancesClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${finances.service.url:http://localhost:8082}")
    private String financesServiceUrl;

    public List<CategorySummaryResponse> getSummaryByCategory(Long userId, String dateFrom, String dateTo) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(financesServiceUrl + "/api/v1/finances/transactions/summary-by-category?dateFrom={dateFrom}&dateTo={dateTo}", dateFrom, dateTo)
                    .header("X-User-Id", String.valueOf(userId))
                    .retrieve()
                    .bodyToFlux(CategorySummaryResponse.class)
                    .collectList()
                    .block();
        } catch (Exception e) {
            log.error("Failed to fetch category summary for userId={}: {}", userId, e.getMessage());
            return List.of();
        }
    }
}
