package com.financialapp.notifications.application.usecase.preference;

import com.financialapp.notifications.domain.model.entity.UserNotificationPreference;
import com.financialapp.notifications.domain.model.response.NotificationPreferenceResponse;
import com.financialapp.notifications.domain.repository.UserNotificationPreferenceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PreferenceUseCasesTest {

    @Mock UserNotificationPreferenceRepository repository;

    private UserNotificationPreference pref(Long userId, String email, boolean enabled) {
        return UserNotificationPreference.builder().id(1L).userId(userId).email(email)
                .monthlyEmailEnabled(enabled).build();
    }

    @Test
    void createIfAbsent_savesWhenMissing() {
        // Given no existing preference
        when(repository.findByUserId(3L)).thenReturn(Optional.empty());

        // When creating if absent
        new CreatePreferenceIfAbsentUseCaseImpl(repository).execute(3L, "n@x.com");

        // Then a new enabled preference is saved
        ArgumentCaptor<UserNotificationPreference> captor =
                ArgumentCaptor.forClass(UserNotificationPreference.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().userId()).isEqualTo(3L);
        assertThat(captor.getValue().email()).isEqualTo("n@x.com");
        assertThat(captor.getValue().monthlyEmailEnabled()).isTrue();
    }

    @Test
    void createIfAbsent_skipsWhenPresent() {
        // Given an existing preference
        when(repository.findByUserId(3L)).thenReturn(Optional.of(pref(3L, "e", true)));

        // When creating if absent / Then nothing is saved
        new CreatePreferenceIfAbsentUseCaseImpl(repository).execute(3L, "n@x.com");
        verify(repository, never()).save(any());
    }

    @Test
    void getPreference_returnsStoredPreference() {
        // Given a stored preference
        when(repository.findByUserId(3L)).thenReturn(Optional.of(pref(3L, "e@x.com", false)));

        // When getting it
        NotificationPreferenceResponse result = new GetPreferenceUseCaseImpl(repository).execute(3L);

        // Then the stored values are returned
        assertThat(result.getUserId()).isEqualTo(3L);
        assertThat(result.getEmail()).isEqualTo("e@x.com");
        assertThat(result.isMonthlyEmailEnabled()).isFalse();
    }

    @Test
    void getPreference_returnsDefaultWhenMissing() {
        // Given no stored preference
        when(repository.findByUserId(3L)).thenReturn(Optional.empty());

        // When getting it
        NotificationPreferenceResponse result = new GetPreferenceUseCaseImpl(repository).execute(3L);

        // Then a default enabled preference with empty email is returned
        assertThat(result.getUserId()).isEqualTo(3L);
        assertThat(result.getEmail()).isEmpty();
        assertThat(result.isMonthlyEmailEnabled()).isTrue();
    }

    @Test
    void updatePreference_togglesExistingPreference() {
        // Given an existing enabled preference echoed back on save
        when(repository.findByUserId(3L)).thenReturn(Optional.of(pref(3L, "e@x.com", true)));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // When disabling monthly email
        NotificationPreferenceResponse result = new UpdatePreferenceUseCaseImpl(repository).execute(3L, false);

        // Then the saved preference reflects the toggle
        assertThat(result.getUserId()).isEqualTo(3L);
        assertThat(result.getEmail()).isEqualTo("e@x.com");
        assertThat(result.isMonthlyEmailEnabled()).isFalse();
    }

    @Test
    void updatePreference_createsDefaultWhenMissing() {
        // Given no existing preference
        when(repository.findByUserId(3L)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // When enabling monthly email
        NotificationPreferenceResponse result = new UpdatePreferenceUseCaseImpl(repository).execute(3L, true);

        // Then a default-email preference is created with the requested flag
        assertThat(result.getUserId()).isEqualTo(3L);
        assertThat(result.getEmail()).isEmpty();
        assertThat(result.isMonthlyEmailEnabled()).isTrue();
    }
}
