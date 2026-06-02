package com.financialapp.notifications.infrastructure.repository;

import com.financialapp.notifications.domain.model.entity.UserNotificationPreference;
import com.financialapp.notifications.domain.model.response.PageResult;
import com.financialapp.notifications.domain.repository.UserNotificationPreferenceRepository;
import com.financialapp.notifications.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/** Round-trips preferences through the real SqlUserNotificationPreferencePersistence + mapper + H2 entity. */
class PreferencePersistenceIT extends IntegrationTestBase {

    @Autowired UserNotificationPreferenceRepository repository;

    @Test
    void save_thenFindByUserId_roundTrips() {
        // Given a new preference / When saved
        UserNotificationPreference saved = repository.save(
                UserNotificationPreference.create(201L, "u201@x.com"));

        // Then it gets an id and timestamps, and is retrievable
        assertThat(saved.id()).isNotNull();
        assertThat(saved.createdAt()).isNotNull();
        Optional<UserNotificationPreference> found = repository.findByUserId(201L);
        assertThat(found).isPresent();
        assertThat(found.get().email()).isEqualTo("u201@x.com");
        assertThat(found.get().monthlyEmailEnabled()).isTrue();
    }

    @Test
    void findByMonthlyEmailEnabledTrue_returnsOnlyEnabled() {
        // Given one enabled and one disabled preference
        repository.save(UserNotificationPreference.create(202L, "enabled@x.com"));
        repository.save(UserNotificationPreference.builder().userId(203L).email("disabled@x.com")
                .monthlyEmailEnabled(false).build());

        // When listing enabled preferences
        PageResult<UserNotificationPreference> page = repository.findByMonthlyEmailEnabledTrue(0, 100);

        // Then only the enabled one(s) are present and the disabled one is excluded
        assertThat(page.content()).extracting(UserNotificationPreference::email)
                .contains("enabled@x.com").doesNotContain("disabled@x.com");
    }

    @Test
    void save_existingPreference_updatesAndStampsUpdatedAt() {
        // Given a stored preference
        UserNotificationPreference saved = repository.save(UserNotificationPreference.create(204L, "u204@x.com"));

        // When toggling monthly email and re-saving
        UserNotificationPreference updated = repository.save(saved.withMonthlyEmailEnabled(false));

        // Then the flag is persisted
        assertThat(updated.monthlyEmailEnabled()).isFalse();
        assertThat(repository.findByUserId(204L)).get()
                .extracting(UserNotificationPreference::monthlyEmailEnabled).isEqualTo(false);
    }
}
