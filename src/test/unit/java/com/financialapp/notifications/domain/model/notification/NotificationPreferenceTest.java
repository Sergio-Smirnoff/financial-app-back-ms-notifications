package com.financialapp.notifications.domain.model.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationPreferenceTest {

    @ParameterizedTest
    @EnumSource(NotificationCategory.class)
    void defaultsHaveInAppEnabledTrueForAllCategories(NotificationCategory category) {
        NotificationPreference pref = NotificationPreference.defaults(100L, category);
        assertThat(pref.userId()).isEqualTo(100L);
        assertThat(pref.category()).isEqualTo(category);
        assertThat(pref.inAppEnabled()).isTrue();
    }

    @Test
    void defaultsSummaryHasEmailEnabledTrue() {
        NotificationPreference pref = NotificationPreference.defaults(100L, NotificationCategory.SUMMARY);
        assertThat(pref.emailEnabled()).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = NotificationCategory.class, names = {"SUMMARY"}, mode = EnumSource.Mode.EXCLUDE)
    void defaultsNonSummaryHaveEmailEnabledFalse(NotificationCategory category) {
        NotificationPreference pref = NotificationPreference.defaults(100L, category);
        assertThat(pref.emailEnabled()).isFalse();
    }

    @Test
    void withChannelsUpdatesValues() {
        NotificationPreference initial = NotificationPreference.defaults(100L, NotificationCategory.PAYMENT_DUE);
        NotificationPreference updated = initial.withChannels(false, true);

        assertThat(updated.userId()).isEqualTo(100L);
        assertThat(updated.category()).isEqualTo(NotificationCategory.PAYMENT_DUE);
        assertThat(updated.inAppEnabled()).isFalse();
        assertThat(updated.emailEnabled()).isTrue();
        assertThat(updated.updatedAt()).isNotNull();
    }
}
