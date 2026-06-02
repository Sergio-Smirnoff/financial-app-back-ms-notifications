package com.financialapp.notifications.domain.model.entity;

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
        UserNotificationPreference pref = UserNotificationPreference.builder()
                .id(1L).userId(2L).email("x@y.com").monthlyEmailEnabled(true).createdAt(created).build();

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
        UserNotificationPreference a = UserNotificationPreference.builder().id(1L).email("e").build();
        UserNotificationPreference b = UserNotificationPreference.builder().id(1L).email("e").build();
        UserNotificationPreference c = UserNotificationPreference.builder().id(2L).email("e").build();

        // Then it behaves as a value record
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b).isNotEqualTo(c);
        assertThat(a.toString()).contains("UserNotificationPreference");
    }
}
