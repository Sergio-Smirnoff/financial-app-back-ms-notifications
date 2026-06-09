package com.financialapp.notifications.application.service;

import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.UserNotificationPreference;
import com.financialapp.notifications.domain.usecase.preference.GetPreferenceUseCase;
import com.financialapp.notifications.domain.usecase.preference.command.GetPreferenceCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationChannelResolverTest {

    @Mock GetPreferenceUseCase getPreferenceUseCase;
    @InjectMocks NotificationChannelResolver resolver;

    @Test
    void resolve_emailEnabled_returnsBoth() {
        when(getPreferenceUseCase.execute(any(GetPreferenceCommand.class)))
                .thenReturn(new UserNotificationPreference(1L, 42L, "user@x.com", true, null, null));

        NotificationChannel result = resolver.resolve(42L);

        assertThat(result).isEqualTo(NotificationChannel.BOTH);
    }

    @Test
    void resolve_emailDisabled_returnsInApp() {
        when(getPreferenceUseCase.execute(any(GetPreferenceCommand.class)))
                .thenReturn(new UserNotificationPreference(1L, 42L, "user@x.com", false, null, null));

        NotificationChannel result = resolver.resolve(42L);

        assertThat(result).isEqualTo(NotificationChannel.IN_APP);
    }

    @Test
    void resolve_noPreference_returnsInApp() {
        when(getPreferenceUseCase.execute(any(GetPreferenceCommand.class)))
                .thenThrow(new RuntimeException("User not found"));

        NotificationChannel result = resolver.resolve(99L);

        assertThat(result).isEqualTo(NotificationChannel.IN_APP);
    }
}
