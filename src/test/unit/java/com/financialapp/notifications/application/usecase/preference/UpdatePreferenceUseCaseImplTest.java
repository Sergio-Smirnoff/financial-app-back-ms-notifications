package com.financialapp.notifications.application.usecase.preference;

import com.financialapp.notifications.application.usecase.preference.impl.UpdatePreferenceUseCaseImpl;
import com.financialapp.notifications.domain.model.notification.NotificationCategory;
import com.financialapp.notifications.domain.model.notification.NotificationPreference;
import com.financialapp.notifications.domain.model.notification.UserNotificationPreference;
import com.financialapp.notifications.domain.repository.NotificationPreferenceRepository;
import com.financialapp.notifications.domain.repository.UserNotificationPreferenceRepository;
import com.financialapp.notifications.domain.usecase.preference.command.UpdatePreferenceCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdatePreferenceUseCaseImplTest {

    @Mock NotificationPreferenceRepository preferenceRepository;
    @Mock UserNotificationPreferenceRepository userNotificationPreferenceRepository;

    private UpdatePreferenceUseCaseImpl useCase() {
        return new UpdatePreferenceUseCaseImpl(preferenceRepository, userNotificationPreferenceRepository);
    }

    @Test
    void execute_whenLegacyRowAbsentButCategoryRowExists_togglesAndUsesPlaceholderEmail() {
        // Given no legacy row but a stored per-category SUMMARY row
        LocalDateTime created = LocalDateTime.of(2026, 8, 5, 9, 0);
        LocalDateTime updated = LocalDateTime.of(2026, 8, 6, 9, 0);
        when(userNotificationPreferenceRepository.findByUserId(8L)).thenReturn(Optional.empty());
        when(preferenceRepository.findByUserIdAndCategory(8L, NotificationCategory.SUMMARY))
                .thenReturn(Optional.of(new NotificationPreference(
                        55L, 8L, NotificationCategory.SUMMARY, true, false, created, updated)));
        when(preferenceRepository.save(any())).thenReturn(new NotificationPreference(
                55L, 8L, NotificationCategory.SUMMARY, true, true, created, updated));

        // When monthly email is enabled
        UserNotificationPreference result = useCase().execute(new UpdatePreferenceCommand(8L, true));

        // Then the stored row is toggled and the placeholder email stands in
        ArgumentCaptor<NotificationPreference> captor = ArgumentCaptor.forClass(NotificationPreference.class);
        verify(preferenceRepository).save(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(55L);
        assertThat(captor.getValue().emailEnabled()).isTrue();
        assertThat(captor.getValue().inAppEnabled()).isTrue();
        assertThat(result.id()).isEqualTo(55L);
        assertThat(result.email()).isEqualTo("user@financialapp.com");
        assertThat(result.monthlyEmailEnabled()).isTrue();
    }

    @Test
    void execute_whenSavedRowHasNoId_fallsBackToPlaceholderId() {
        // Given a legacy row and a save that echoes back an unpersisted row
        when(userNotificationPreferenceRepository.findByUserId(8L))
                .thenReturn(Optional.of(new UserNotificationPreference(1L, 8L, "e@x.com", true, null, null)));
        when(preferenceRepository.findByUserIdAndCategory(8L, NotificationCategory.SUMMARY))
                .thenReturn(Optional.of(new NotificationPreference(
                        null, 8L, NotificationCategory.SUMMARY, true, true, null, null)));
        when(preferenceRepository.save(any())).thenReturn(new NotificationPreference(
                null, 8L, NotificationCategory.SUMMARY, true, false, null, null));

        // When monthly email is disabled
        UserNotificationPreference result = useCase().execute(new UpdatePreferenceCommand(8L, false));

        // Then the placeholder id stands in and the legacy email is kept
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.email()).isEqualTo("e@x.com");
        assertThat(result.monthlyEmailEnabled()).isFalse();
    }
}
