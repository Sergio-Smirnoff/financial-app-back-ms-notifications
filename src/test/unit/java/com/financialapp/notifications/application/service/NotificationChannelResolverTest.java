package com.financialapp.notifications.application.service;

import com.financialapp.notifications.domain.model.notification.NotificationCategory;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationPreference;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import com.financialapp.notifications.domain.repository.NotificationPreferenceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationChannelResolverTest {

    @Mock
    private NotificationPreferenceRepository preferenceRepository;

    @InjectMocks
    private NotificationChannelResolver resolver;

    @Test
    void resolve_paymentDueEmailDisabled_returnsInApp_evenIfSummaryEmailEnabled() {
        NotificationPreference paymentDuePref = new NotificationPreference(
                1L, 42L, NotificationCategory.PAYMENT_DUE, true, false, null, null);

        when(preferenceRepository.findByUserIdAndCategory(42L, NotificationCategory.PAYMENT_DUE))
                .thenReturn(Optional.of(paymentDuePref));

        Optional<NotificationChannel> result = resolver.resolve(42L, NotificationType.PAYMENT_DUE);

        assertThat(result).contains(NotificationChannel.IN_APP);
    }

    @Test
    void resolve_lazyDefaultCreationWhenAbsent() {
        when(preferenceRepository.findByUserIdAndCategory(42L, NotificationCategory.BUDGET))
                .thenReturn(Optional.empty());
        when(preferenceRepository.save(any(NotificationPreference.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Optional<NotificationChannel> result = resolver.resolve(42L, NotificationType.BUDGET_THRESHOLD_REACHED);

        assertThat(result).contains(NotificationChannel.IN_APP);
        verify(preferenceRepository).save(any(NotificationPreference.class));
    }

    @Test
    void resolve_neitherChannelEnabled_returnsEmpty() {
        NotificationPreference disabledPref = new NotificationPreference(
                1L, 42L, NotificationCategory.PAYMENT_DUE, false, false, null, null);

        when(preferenceRepository.findByUserIdAndCategory(42L, NotificationCategory.PAYMENT_DUE))
                .thenReturn(Optional.of(disabledPref));

        Optional<NotificationChannel> result = resolver.resolve(42L, NotificationType.PAYMENT_DUE);

        assertThat(result).isEmpty();
    }

    @Test
    void resolve_monthlySummaryWithBothEnabled_returnsBoth() {
        NotificationPreference summaryPref = new NotificationPreference(
                1L, 42L, NotificationCategory.SUMMARY, true, true, null, null);

        when(preferenceRepository.findByUserIdAndCategory(42L, NotificationCategory.SUMMARY))
                .thenReturn(Optional.of(summaryPref));

        Optional<NotificationChannel> result = resolver.resolve(42L, NotificationType.MONTHLY_SUMMARY);

        assertThat(result).contains(NotificationChannel.BOTH);
    }

    @Test
    void resolve_onlyEmailEnabled_returnsEmail() {
        NotificationPreference pref = new NotificationPreference(
                1L, 42L, NotificationCategory.PORTFOLIO_ALERTS, false, true, null, null);

        when(preferenceRepository.findByUserIdAndCategory(42L, NotificationCategory.PORTFOLIO_ALERTS))
                .thenReturn(Optional.of(pref));

        Optional<NotificationChannel> result = resolver.resolve(42L, NotificationType.INVESTMENT_THRESHOLD);

        assertThat(result).contains(NotificationChannel.EMAIL);
    }
}
