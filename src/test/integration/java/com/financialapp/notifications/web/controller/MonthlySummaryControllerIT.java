package com.financialapp.notifications.web.controller;

import com.financialapp.notifications.domain.gateway.FinancesGateway;
import com.financialapp.notifications.domain.messaging.EmailSender;
import com.financialapp.notifications.domain.model.notification.NotificationCategory;
import com.financialapp.notifications.domain.model.notification.NotificationPreference;
import com.financialapp.notifications.domain.model.notification.UserNotificationPreference;
import com.financialapp.notifications.application.service.NotificationServiceImpl;
import com.financialapp.notifications.domain.repository.NotificationPreferenceRepository;
import com.financialapp.notifications.domain.repository.UserNotificationPreferenceRepository;
import com.financialapp.notifications.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class MonthlySummaryControllerIT extends IntegrationTestBase {

    private static final String URL = "/api/v1/notifications/monthly-summary/ensure";
    private static final String TOKEN = "test-token";
    private static final String PREVIOUS_MONTH =
            YearMonth.now(ZoneId.of("America/Argentina/Buenos_Aires")).minusMonths(1).toString();

    @Autowired private MockMvc mockMvc;
    @Autowired private UserNotificationPreferenceRepository preferenceRepository;
    @Autowired private NotificationPreferenceRepository categoryPreferences;
    @MockBean private FinancesGateway financesGateway;
    @MockBean private EmailSender emailSender;
    @MockBean private NotificationServiceImpl notificationService;

    private ResultActions ensure(String userId) throws Exception {
        return mockMvc.perform(post(URL).header("X-User-Id", userId).header("X-Internal-Token", TOKEN));
    }

    @Test
    void ensure_sendsOnce_thenAnswersAlreadySent() throws Exception {
        preferenceRepository.save(UserNotificationPreference.create(701L, "u701@example.com"));
        when(financesGateway.getSummaryByCategory(anyLong(), anyString(), anyString())).thenReturn(List.of());

        ensure("701").andExpect(status().isOk())
                .andExpect(jsonPath("$.data.sent").value(true))
                .andExpect(jsonPath("$.data.month").value(PREVIOUS_MONTH));
        ensure("701").andExpect(status().isOk())
                .andExpect(jsonPath("$.data.sent").value(false))
                .andExpect(jsonPath("$.data.reason").value("already-sent"));

        verify(emailSender, times(1)).sendTemplatedEmail(eq("u701@example.com"), anyString(), eq("monthly-summary"), any());
    }

    @Test
    void ensure_oneUsersRecord_leavesAnotherUserUntouched() throws Exception {
        preferenceRepository.save(UserNotificationPreference.create(702L, "u702@example.com"));
        preferenceRepository.save(UserNotificationPreference.create(703L, "u703@example.com"));
        when(financesGateway.getSummaryByCategory(anyLong(), anyString(), anyString())).thenReturn(List.of());

        ensure("702").andExpect(jsonPath("$.data.sent").value(true));
        ensure("703").andExpect(jsonPath("$.data.sent").value(true));

        verify(emailSender).sendTemplatedEmail(eq("u702@example.com"), anyString(), eq("monthly-summary"), any());
        verify(emailSender).sendTemplatedEmail(eq("u703@example.com"), anyString(), eq("monthly-summary"), any());
    }

    @Test
    void ensure_summaryEmailOffInSettings_isOptedOut_andUnknownUserIsNoRecipient() throws Exception {
        preferenceRepository.save(UserNotificationPreference.create(704L, "u704@example.com"));
        categoryPreferences.save(NotificationPreference.defaults(704L, NotificationCategory.SUMMARY).withChannels(true, false));

        ensure("704").andExpect(jsonPath("$.data.sent").value(false)).andExpect(jsonPath("$.data.reason").value("opted-out"));
        ensure("799").andExpect(jsonPath("$.data.sent").value(false)).andExpect(jsonPath("$.data.reason").value("no-recipient"));

        verify(emailSender, never()).sendTemplatedEmail(anyString(), anyString(), anyString(), any());
    }

    @Test
    void ensure_followsTheSummarySetting_notTheLegacyFlag() throws Exception {
        preferenceRepository.save(UserNotificationPreference.create(705L, "u705@example.com").withMonthlyEmailEnabled(false));
        categoryPreferences.save(NotificationPreference.defaults(705L, NotificationCategory.SUMMARY).withChannels(true, true));
        when(financesGateway.getSummaryByCategory(anyLong(), anyString(), anyString())).thenReturn(List.of());

        ensure("705").andExpect(jsonPath("$.data.sent").value(true));
    }

    @Test
    void ensure_financesFailure_is500_andTheMonthStaysOpen() throws Exception {
        preferenceRepository.save(UserNotificationPreference.create(706L, "u706@example.com"));
        when(financesGateway.getSummaryByCategory(anyLong(), anyString(), anyString()))
                .thenThrow(new IllegalStateException("ms-finances down"))
                .thenReturn(List.of());

        ensure("706").andExpect(status().isInternalServerError());
        ensure("706").andExpect(jsonPath("$.data.sent").value(true));
        verify(emailSender, times(1)).sendTemplatedEmail(eq("u706@example.com"), anyString(), eq("monthly-summary"), any());
    }

    @Test
    void ensure_withoutTheInternalToken_is401() throws Exception {
        mockMvc.perform(post(URL).header("X-User-Id", "701")).andExpect(status().isUnauthorized());
    }
}
