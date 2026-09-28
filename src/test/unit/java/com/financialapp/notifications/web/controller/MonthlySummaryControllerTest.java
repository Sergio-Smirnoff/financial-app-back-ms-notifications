package com.financialapp.notifications.web.controller;

import com.financialapp.notifications.domain.usecase.notification.EnsureMonthlySummaryUseCase;
import com.financialapp.notifications.domain.usecase.notification.MonthlySummaryResult;
import com.financialapp.notifications.domain.usecase.notification.MonthlySummarySkip;
import com.financialapp.notifications.domain.usecase.notification.command.EnsureMonthlySummaryCommand;
import com.financialapp.notifications.web.error.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.YearMonth;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MonthlySummaryControllerTest {

    private static final String URL = "/api/v1/notifications/monthly-summary/ensure";
    private static final YearMonth AUGUST = YearMonth.of(2026, 8);

    @Mock private EnsureMonthlySummaryUseCase ensureMonthlySummaryUseCase;
    @InjectMocks private MonthlySummaryController controller;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void ensure_sent_answersTheMonthWithoutAReason() throws Exception {
        when(ensureMonthlySummaryUseCase.execute(new EnsureMonthlySummaryCommand(42L)))
                .thenReturn(new MonthlySummaryResult(true, AUGUST, null));

        mockMvc.perform(post(URL).header("X-User-Id", "42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.sent").value(true))
                .andExpect(jsonPath("$.data.month").value("2026-08"))
                .andExpect(jsonPath("$.data.reason").doesNotExist());
    }

    @Test
    void ensure_skipped_namesTheReason() throws Exception {
        when(ensureMonthlySummaryUseCase.execute(new EnsureMonthlySummaryCommand(42L)))
                .thenReturn(new MonthlySummaryResult(false, AUGUST, MonthlySummarySkip.ALREADY_SENT));

        mockMvc.perform(post(URL).header("X-User-Id", "42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.sent").value(false))
                .andExpect(jsonPath("$.data.reason").value("already-sent"));
    }
}
