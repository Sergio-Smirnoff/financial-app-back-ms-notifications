package com.financialapp.notifications.application.service;

import com.financialapp.notifications.domain.gateway.FinancesGateway;
import com.financialapp.notifications.domain.messaging.EmailSender;
import com.financialapp.notifications.domain.model.category.CategorySummary;
import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.notification.NotificationCategory;
import com.financialapp.notifications.domain.model.notification.NotificationPreference;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import com.financialapp.notifications.domain.model.notification.UserNotificationPreference;
import com.financialapp.notifications.domain.repository.MonthlySummarySentRepository;
import com.financialapp.notifications.domain.repository.NotificationPreferenceRepository;
import com.financialapp.notifications.domain.service.NotificationService;
import com.financialapp.notifications.domain.usecase.notification.MonthlySummaryResult;
import com.financialapp.notifications.domain.usecase.notification.MonthlySummarySkip;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MonthlySummaryDeliveryTest {

    private static final YearMonth AUGUST = YearMonth.of(2026, 8);
    private static final UserNotificationPreference RECIPIENT =
            new UserNotificationPreference(1L, 1L, "u1@example.com", false, null, null);

    @Mock private NotificationPreferenceRepository categoryPreferences;
    @Mock private MonthlySummarySentRepository sentRepository;
    @Mock private FinancesGateway financesGateway;
    @Mock private NotificationService notificationService;
    @Mock private EmailSender emailSender;
    @InjectMocks private MonthlySummaryDelivery delivery;
    @Captor private ArgumentCaptor<Map<String, Object>> templateVars;
    @Captor private ArgumentCaptor<Notification> notification;

    private void summaryEmail(boolean enabled) {
        when(categoryPreferences.findByUserIdAndCategory(1L, NotificationCategory.SUMMARY)).thenReturn(Optional.of(
                NotificationPreference.defaults(1L, NotificationCategory.SUMMARY).withChannels(true, enabled)));
    }

    @Test
    void deliver_summaryEmailOff_isOptedOut_whateverTheLegacyFlagSays() {
        summaryEmail(false);

        assertThat(delivery.deliver(RECIPIENT, AUGUST))
                .isEqualTo(new MonthlySummaryResult(false, AUGUST, MonthlySummarySkip.OPTED_OUT));
        verifyNoInteractions(sentRepository, financesGateway, notificationService, emailSender);
    }

    @Test
    void deliver_monthAlreadyClaimed_sendsNothing() {
        summaryEmail(true);
        when(sentRepository.claim(1L, AUGUST)).thenReturn(false);

        assertThat(delivery.deliver(RECIPIENT, AUGUST))
                .isEqualTo(new MonthlySummaryResult(false, AUGUST, MonthlySummarySkip.ALREADY_SENT));
        verifyNoInteractions(financesGateway, notificationService, emailSender);
    }

    @Test
    void deliver_noSummaryRow_followsTheCategoryDefault_andSendsEmailThenNotice() {
        when(categoryPreferences.findByUserIdAndCategory(1L, NotificationCategory.SUMMARY)).thenReturn(Optional.empty());
        when(sentRepository.claim(1L, AUGUST)).thenReturn(true);
        when(financesGateway.getSummaryByCategory(1L, "2026-08-01", "2026-08-31")).thenReturn(List.of(
                new CategorySummary("Food", null, new BigDecimal("100"), "ARS", 2L)));

        assertThat(delivery.deliver(RECIPIENT, AUGUST)).isEqualTo(new MonthlySummaryResult(true, AUGUST, null));

        InOrder order = inOrder(emailSender, notificationService);
        order.verify(emailSender).sendTemplatedEmail(eq("u1@example.com"), anyString(), eq("monthly-summary"), templateVars.capture());
        order.verify(notificationService).notify(notification.capture());
        assertThat(templateVars.getValue().get("message").toString()).contains("Food", "ARS");
        assertThat(notification.getValue().type()).isEqualTo(NotificationType.MONTHLY_SUMMARY);
        verify(sentRepository, never()).release(anyLong(), any());
    }

    @Test
    void deliver_monthWithoutExpenses_sendsTheEmptyMessage() {
        summaryEmail(true);
        when(sentRepository.claim(1L, AUGUST)).thenReturn(true);
        when(financesGateway.getSummaryByCategory(1L, "2026-08-01", "2026-08-31")).thenReturn(List.of());

        delivery.deliver(RECIPIENT, AUGUST);

        verify(emailSender).sendTemplatedEmail(eq("u1@example.com"), anyString(), eq("monthly-summary"), templateVars.capture());
        assertThat(templateVars.getValue().get("message")).isEqualTo("No tuviste transacciones este mes.");
    }

    @Test
    void deliver_emailFailure_releasesTheMonth_rethrows_andNeverNotifies() {
        summaryEmail(true);
        when(sentRepository.claim(1L, AUGUST)).thenReturn(true);
        when(financesGateway.getSummaryByCategory(anyLong(), anyString(), anyString())).thenReturn(List.of());
        doThrow(new RuntimeException("smtp down")).when(emailSender)
                .sendTemplatedEmail(anyString(), anyString(), anyString(), any());

        assertThatThrownBy(() -> delivery.deliver(RECIPIENT, AUGUST)).hasMessage("smtp down");
        verify(sentRepository).release(1L, AUGUST);
        verifyNoInteractions(notificationService);
    }

    @Test
    void deliver_financesFailure_releasesTheMonth_andSendsNothing() {
        summaryEmail(true);
        when(sentRepository.claim(1L, AUGUST)).thenReturn(true);
        when(financesGateway.getSummaryByCategory(anyLong(), anyString(), anyString()))
                .thenThrow(new IllegalStateException("ms-finances down"));

        assertThatThrownBy(() -> delivery.deliver(RECIPIENT, AUGUST)).hasMessage("ms-finances down");
        verify(sentRepository).release(1L, AUGUST);
        verifyNoInteractions(emailSender, notificationService);
    }

    @Test
    void deliver_notifyFailure_afterEmailSent_stillCountsAsSent_andNeverReleases() {
        summaryEmail(true);
        when(sentRepository.claim(1L, AUGUST)).thenReturn(true);
        when(financesGateway.getSummaryByCategory(anyLong(), anyString(), anyString())).thenReturn(List.of());
        doThrow(new RuntimeException("notification persistence down")).when(notificationService).notify(any());

        assertThat(delivery.deliver(RECIPIENT, AUGUST)).isEqualTo(new MonthlySummaryResult(true, AUGUST, null));

        verify(emailSender, times(1)).sendTemplatedEmail(eq("u1@example.com"), anyString(), eq("monthly-summary"), any());
        verify(sentRepository, never()).release(anyLong(), any());
    }
}
