package com.financialapp.notifications.infrastructure.persistence.entity;

import com.financialapp.notifications.domain.model.notification.NotificationCategory;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationPreferenceSqlEntityTest {

    @Test
    void builderAndAccessors_roundTripFields() {
        // Given / When built
        NotificationPreferenceSqlEntity entity = NotificationPreferenceSqlEntity.builder()
                .id(1L).userId(2L).category(NotificationCategory.PAYMENT_DUE)
                .inAppEnabled(true).emailEnabled(false).build();

        // Then accessors expose the fields
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getUserId()).isEqualTo(2L);
        assertThat(entity.getCategory()).isEqualTo(NotificationCategory.PAYMENT_DUE);
        assertThat(entity.isInAppEnabled()).isTrue();
        assertThat(entity.isEmailEnabled()).isFalse();
    }

    @Test
    void onCreate_stampsCreatedAndUpdatedAt() {
        // Given a fresh entity
        NotificationPreferenceSqlEntity entity = new NotificationPreferenceSqlEntity();
        assertThat(entity.getCreatedAt()).isNull();

        // When the @PrePersist hook runs
        entity.onCreate();

        // Then both timestamps are stamped
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotNull();
    }

    @Test
    void onCreate_whenTimestampsAlreadySet_keepsThem() {
        // Given an entity carrying explicit timestamps
        LocalDateTime created = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime updated = LocalDateTime.of(2026, 1, 2, 0, 0);
        NotificationPreferenceSqlEntity entity = new NotificationPreferenceSqlEntity();
        entity.setCreatedAt(created);
        entity.setUpdatedAt(updated);

        // When the @PrePersist hook runs
        entity.onCreate();

        // Then the supplied timestamps are left untouched
        assertThat(entity.getCreatedAt()).isEqualTo(created);
        assertThat(entity.getUpdatedAt()).isEqualTo(updated);
    }

    @Test
    void onUpdate_refreshesUpdatedAt() {
        // Given an entity with no updatedAt
        NotificationPreferenceSqlEntity entity = new NotificationPreferenceSqlEntity();
        assertThat(entity.getUpdatedAt()).isNull();

        // When the @PreUpdate hook runs
        entity.onUpdate();

        // Then updatedAt is set
        assertThat(entity.getUpdatedAt()).isNotNull();
    }
}
