package com.financialapp.notifications.domain.model.entity;

import com.financialapp.notifications.domain.model.notification.UserNotificationPreference;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserNotificationPreferenceTest {

    @Test
    void create_enablesMonthlyEmailByDefault() {
        // Given user data / When created via the factory
        UserNotificationPreference pref = UserNotificationPreference.create(5L, "a@b.com");

        // Then it is enabled with the given identity, no id/timestamps yet
        assertThat(pref.userId()).isEqualTo(5L);
        assertThat(pref.email()).isEqualTo("a@b.com");
        assertThat(pref.monthlyEmailEnabled()).isTrue();
        assertThat(pref.id()).isNull();
        assertThat(pref.updatedAt()).isNull();
    }

    @Test
    void withMonthlyEmailEnabled_togglesFlagAndStampsUpdatedAt() {
        // Given an existing preference with no updatedAt
        LocalDateTime created = LocalDateTime.of(2026, 1, 1, 0, 0);
        UserNotificationPreference pref = new UserNotificationPreference(1L, 2L, "x@y.com", true, created, null);

        // When disabling monthly email
        UserNotificationPreference updated = pref.withMonthlyEmailEnabled(false);

        // Then the flag flips, createdAt is preserved and updatedAt is stamped
        assertThat(updated.monthlyEmailEnabled()).isFalse();
        assertThat(updated.id()).isEqualTo(1L);
        assertThat(updated.userId()).isEqualTo(2L);
        assertThat(updated.email()).isEqualTo("x@y.com");
        assertThat(updated.createdAt()).isEqualTo(created);
        assertThat(updated.updatedAt()).isNotNull();
    }

    @Test
    void equalsHashCodeToString_distinguishInstances() {
        // Given two equal and one different preference
        UserNotificationPreference a = new UserNotificationPreference(1L, null, "e", false, null, null);
        UserNotificationPreference b = new UserNotificationPreference(1L, null, "e", false, null, null);
        UserNotificationPreference c = new UserNotificationPreference(2L, null, "e", false, null, null);

        // Then it behaves as a value record
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b).isNotEqualTo(c);
        assertThat(a.toString()).contains("UserNotificationPreference");
    }
}
