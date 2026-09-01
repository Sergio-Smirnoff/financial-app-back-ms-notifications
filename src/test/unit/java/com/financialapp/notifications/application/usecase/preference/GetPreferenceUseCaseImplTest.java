package com.financialapp.notifications.application.usecase.preference;

import com.financialapp.notifications.application.usecase.preference.impl.GetPreferenceUseCaseImpl;
import com.financialapp.notifications.domain.model.notification.NotificationCategory;
import com.financialapp.notifications.domain.model.notification.NotificationPreference;
import com.financialapp.notifications.domain.model.notification.UserNotificationPreference;
import com.financialapp.notifications.domain.repository.NotificationPreferenceRepository;
import com.financialapp.notifications.domain.repository.UserNotificationPreferenceRepository;
import com.financialapp.notifications.domain.usecase.preference.command.GetPreferenceCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetPreferenceUseCaseImplTest {

    @Mock NotificationPreferenceRepository preferenceRepository;
    @Mock UserNotificationPreferenceRepository userNotificationPreferenceRepository;

    private GetPreferenceUseCaseImpl useCase() {
        return new GetPreferenceUseCaseImpl(preferenceRepository, userNotificationPreferenceRepository);
    }

    @Test
    void execute_whenLegacyRowAbsentButCategoryRowExists_fallsBackToPlaceholderEmail() {
        // Given no legacy row but a per-category SUMMARY row carrying an id
        LocalDateTime created = LocalDateTime.of(2026, 8, 5, 9, 0);
        LocalDateTime updated = LocalDateTime.of(2026, 8, 6, 9, 0);
        when(userNotificationPreferenceRepository.findByUserId(8L)).thenReturn(Optional.empty());
        when(preferenceRepository.findByUserIdAndCategory(8L, NotificationCategory.SUMMARY))
                .thenReturn(Optional.of(new NotificationPreference(
                        55L, 8L, NotificationCategory.SUMMARY, true, true, created, updated)));

        // When the preference is read
        UserNotificationPreference result = useCase().execute(new GetPreferenceCommand(8L));

        // Then the category row answers the read and the placeholder email stands in
        assertThat(result.id()).isEqualTo(55L);
        assertThat(result.userId()).isEqualTo(8L);
        assertThat(result.email()).isEqualTo("user@financialapp.com");
        assertThat(result.monthlyEmailEnabled()).isTrue();
        assertThat(result.createdAt()).isEqualTo(created);
        assertThat(result.updatedAt()).isEqualTo(updated);
        verify(preferenceRepository, never()).save(any());
    }

    @Test
    void execute_whenCategoryRowHasNoId_fallsBackToPlaceholderId() {
        // Given a legacy row and an unpersisted per-category SUMMARY row
        when(userNotificationPreferenceRepository.findByUserId(8L))
                .thenReturn(Optional.of(new UserNotificationPreference(1L, 8L, "e@x.com", true, null, null)));
        when(preferenceRepository.findByUserIdAndCategory(8L, NotificationCategory.SUMMARY))
                .thenReturn(Optional.of(new NotificationPreference(
                        null, 8L, NotificationCategory.SUMMARY, true, false, null, null)));

        // When the preference is read
        UserNotificationPreference result = useCase().execute(new GetPreferenceCommand(8L));

        // Then the placeholder id stands in and the legacy email is kept
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.email()).isEqualTo("e@x.com");
        assertThat(result.monthlyEmailEnabled()).isFalse();
    }
}
