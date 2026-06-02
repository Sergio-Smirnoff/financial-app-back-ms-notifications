package com.financialapp.notifications.web.controller;

import com.financialapp.notifications.domain.model.response.NotificationResponse;
import com.financialapp.notifications.domain.usecase.GetLatestNotificationsByBankUseCase;
import com.financialapp.notifications.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Covers the {@code bankId != null} branch of NotificationController#getLatest. The by-bank query
 * uses a PostgreSQL-only {@code metadata->>'bankId'} native query that H2 cannot execute, so the
 * by-bank use case (fully unit-tested elsewhere) is stubbed here purely to exercise the controller
 * branch. See general-analysis/project-inconsistencies.md.
 */
@AutoConfigureMockMvc
class NotificationByBankControllerIT extends IntegrationTestBase {

    @Autowired MockMvc mockMvc;
    @MockBean GetLatestNotificationsByBankUseCase byBankUseCase;

    @Test
    void getLatest_withBankId_delegatesToByBankUseCase() throws Exception {
        // Given the by-bank use case returns one notification
        when(byBankUseCase.execute(eq(401L), eq(42L)))
                .thenReturn(List.of(NotificationResponse.builder().id(1L).type("PAYMENT_DUE").build()));

        // When fetching latest with a bankId filter
        mockMvc.perform(get("/api/v1/notifications/latest")
                        .param("bankId", "42")
                        .header("X-User-Id", "401").header("X-Internal-Token", "test-token"))
                // Then the by-bank branch is taken and the result returned
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].type").value("PAYMENT_DUE"));
        verify(byBankUseCase).execute(401L, 42L);
    }
}
