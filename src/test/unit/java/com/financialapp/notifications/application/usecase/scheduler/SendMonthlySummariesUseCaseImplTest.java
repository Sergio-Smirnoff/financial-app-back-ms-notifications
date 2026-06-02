package com.financialapp.notifications.application.usecase.scheduler;

import com.financialapp.notifications.application.service.NotificationService;
import com.financialapp.notifications.domain.gateway.FinancesGateway;
import com.financialapp.notifications.domain.messaging.EmailSender;
import com.financialapp.notifications.domain.model.entity.Notification;
import com.financialapp.notifications.domain.model.entity.UserNotificationPreference;
import com.financialapp.notifications.domain.model.entity.enums.NotificationType;
import com.financialapp.notifications.domain.model.entity.summary.CategorySummary;
import com.financialapp.notifications.domain.model.response.PageResult;
import com.financialapp.notifications.domain.repository.UserNotificationPreferenceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SendMonthlySummariesUseCaseImplTest {

    @Mock UserNotificationPreferenceRepository preferenceRepository;
    @Mock FinancesGateway financesGateway;
    @Mock NotificationService notificationService;
    @Mock EmailSender emailSender;
    @InjectMocks SendMonthlySummariesUseCaseImpl useCase;

    private UserNotificationPreference pref(Long userId, String email) {
        return UserNotificationPreference.builder().id(userId).userId(userId).email(email)
                .monthlyEmailEnabled(true).build();
    }

    @Test
    void execute_pagesUntilLastPage_andSendsSummariesWithCategoryMessage() {
        // Given two pages of preferences (first page hasNext, second is last)
        UserNotificationPreference u1 = pref(1L, "u1@x.com");
        UserNotificationPreference u2 = pref(2L, "u2@x.com");
        when(preferenceRepository.findByMonthlyEmailEnabledTrue(0, 500))
                .thenReturn(new PageResult<>(List.of(u1), 0, 500, 1000));
        when(preferenceRepository.findByMonthlyEmailEnabledTrue(1, 500))
                .thenReturn(new PageResult<>(List.of(u2), 1, 500, 1000));
        // And a non-empty summary for user 1, empty for user 2
        when(financesGateway.getSummaryByCategory(eq(1L), anyString(), anyString())).thenReturn(List.of(
                CategorySummary.builder().categoryName("Food").currency("ARS")
                        .totalAmount(new BigDecimal("100")).transactionCount(2L).build()));
        when(financesGateway.getSummaryByCategory(eq(2L), anyString(), anyString())).thenReturn(List.of());

        // When the job runs
        useCase.execute();

        // Then both users get a MONTHLY_SUMMARY notification and a templated email
        ArgumentCaptor<Notification> notifCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationService, times(2)).notify(notifCaptor.capture());
        assertThat(notifCaptor.getAllValues())
                .allMatch(n -> n.type() == NotificationType.MONTHLY_SUMMARY);

        // And the email body reflects the category breakdown vs the empty fallback
        ArgumentCaptor<Map<String, Object>> varsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(emailSender).sendTemplatedEmail(eq("u1@x.com"), anyString(), eq("monthly-summary"), varsCaptor.capture());
        assertThat(varsCaptor.getValue().get("message").toString()).contains("Food", "ARS");
        verify(emailSender).sendTemplatedEmail(eq("u2@x.com"), anyString(), eq("monthly-summary"), any());
    }

    @Test
    void execute_swallowsPerUserFailure() {
        // Given a single page whose only user fails during email sending
        when(preferenceRepository.findByMonthlyEmailEnabledTrue(0, 500))
                .thenReturn(new PageResult<>(List.of(pref(9L, "fail@x.com")), 0, 500, 1));
        when(financesGateway.getSummaryByCategory(eq(9L), anyString(), anyString())).thenReturn(List.of());
        doThrow(new RuntimeException("smtp down")).when(emailSender)
                .sendTemplatedEmail(anyString(), anyString(), anyString(), any());

        // When the job runs / Then the failure is swallowed (no exception propagates)
        useCase.execute();

        // And the notification was still attempted before the email failed
        verify(notificationService).notify(any());
    }
}
