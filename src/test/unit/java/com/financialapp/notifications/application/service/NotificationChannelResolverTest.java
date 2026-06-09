package com.financialapp.notifications.application.service;

import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.UserNotificationPreference;
import com.financialapp.notifications.domain.repository.UserNotificationPreferenceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationChannelResolverTest {

    @Mock UserNotificationPreferenceRepository preferenceRepository;
    @InjectMocks NotificationChannelResolver resolver;

    @Test
    void resolve_emailEnabled_returnsBoth() {
        when(preferenceRepository.findByUserId(42L)).thenReturn(Optional.of(
                new UserNotificationPreference(1L, 42L, "user@x.com", true, null, null)));

        assertThat(resolver.resolve(42L)).isEqualTo(NotificationChannel.BOTH);
    }

    @Test
    void resolve_emailDisabled_returnsInApp() {
        when(preferenceRepository.findByUserId(42L)).thenReturn(Optional.of(
                new UserNotificationPreference(1L, 42L, "user@x.com", false, null, null)));

        assertThat(resolver.resolve(42L)).isEqualTo(NotificationChannel.IN_APP);
    }

    @Test
    void resolve_noPreference_returnsInApp() {
        when(preferenceRepository.findByUserId(99L)).thenReturn(Optional.empty());

        assertThat(resolver.resolve(99L)).isEqualTo(NotificationChannel.IN_APP);
    }
}
