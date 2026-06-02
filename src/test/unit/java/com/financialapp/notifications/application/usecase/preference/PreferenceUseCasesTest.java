package com.financialapp.notifications.application.usecase.preference;

import com.financialapp.notifications.application.usecase.preference.impl.CreatePreferenceIfAbsentUseCaseImpl;
import com.financialapp.notifications.application.usecase.preference.impl.GetPreferenceUseCaseImpl;
import com.financialapp.notifications.application.usecase.preference.impl.UpdatePreferenceUseCaseImpl;
import com.financialapp.notifications.domain.model.notification.UserNotificationPreference;
import com.financialapp.notifications.domain.repository.UserNotificationPreferenceRepository;
import com.financialapp.notifications.domain.usecase.preference.command.CreatePreferenceIfAbsentCommand;
import com.financialapp.notifications.domain.usecase.preference.command.GetPreferenceCommand;
import com.financialapp.notifications.domain.usecase.preference.command.UpdatePreferenceCommand;
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
        return new UserNotificationPreference(1L, userId, email, enabled, null, null);
    }

    @Test
    void createIfAbsent_savesWhenMissing() {
        // Given no existing preference
        when(repository.findByUserId(3L)).thenReturn(Optional.empty());

        // When creating if absent
        new CreatePreferenceIfAbsentUseCaseImpl(repository).execute(new CreatePreferenceIfAbsentCommand(3L, "n@x.com"));

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
        new CreatePreferenceIfAbsentUseCaseImpl(repository).execute(new CreatePreferenceIfAbsentCommand(3L, "n@x.com"));
        verify(repository, never()).save(any());
    }

    @Test
    void getPreference_returnsStoredPreference() {
        // Given a stored preference
        when(repository.findByUserId(3L)).thenReturn(Optional.of(pref(3L, "e@x.com", false)));

        // When getting it
        UserNotificationPreference result = new GetPreferenceUseCaseImpl(repository).execute(new GetPreferenceCommand(3L));

        // Then the stored values are returned
        assertThat(result.userId()).isEqualTo(3L);
        assertThat(result.email()).isEqualTo("e@x.com");
        assertThat(result.monthlyEmailEnabled()).isFalse();
    }

    @Test
    void getPreference_returnsDefaultWhenMissing() {
        // Given no stored preference
        when(repository.findByUserId(3L)).thenReturn(Optional.empty());

        // When getting it / Then a UserNotFoundException is thrown (preference not found)
        org.junit.jupiter.api.Assertions.assertThrows(
                com.financialapp.notifications.domain.exception.UserNotFoundException.class,
                () -> new GetPreferenceUseCaseImpl(repository).execute(new GetPreferenceCommand(3L)));
    }

    @Test
    void updatePreference_togglesExistingPreference() {
        // Given an existing enabled preference echoed back on save
        when(repository.findByUserId(3L)).thenReturn(Optional.of(pref(3L, "e@x.com", true)));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // When disabling monthly email
        UserNotificationPreference result = new UpdatePreferenceUseCaseImpl(repository).execute(new UpdatePreferenceCommand(3L, false));

        // Then the saved preference reflects the toggle
        assertThat(result.userId()).isEqualTo(3L);
        assertThat(result.email()).isEqualTo("e@x.com");
        assertThat(result.monthlyEmailEnabled()).isFalse();
    }

    @Test
    void updatePreference_createsDefaultWhenMissing() {
        // Given no existing preference
        when(repository.findByUserId(3L)).thenReturn(Optional.empty());

        // When enabling monthly email / Then a UserNotFoundException is thrown (preference not found)
        org.junit.jupiter.api.Assertions.assertThrows(
                com.financialapp.notifications.domain.exception.UserNotFoundException.class,
                () -> new UpdatePreferenceUseCaseImpl(repository).execute(new UpdatePreferenceCommand(3L, true)));
    }
}
