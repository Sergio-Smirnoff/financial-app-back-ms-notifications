package com.financialapp.notifications.infrastructure.repository.preferences;

import com.financialapp.notifications.infrastructure.persistence.entity.UserNotificationPreferenceSqlEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserNotificationPreferenceSqlEntityTest {

    @Test
    void builderAndAccessors_roundTripFields() {
        // Given / When built
        UserNotificationPreferenceSqlEntity entity = UserNotificationPreferenceSqlEntity.builder()
                .id(1L).userId(2L).email("e@x.com").monthlyEmailEnabled(false).build();

        // Then accessors expose the fields
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getUserId()).isEqualTo(2L);
        assertThat(entity.getEmail()).isEqualTo("e@x.com");
        assertThat(entity.isMonthlyEmailEnabled()).isFalse();
    }

    @Test
    void onCreate_stampsCreatedAndUpdatedAt() {
        // Given a fresh entity
        UserNotificationPreferenceSqlEntity entity = new UserNotificationPreferenceSqlEntity();
        assertThat(entity.getCreatedAt()).isNull();

        // When the @PrePersist hook runs
        entity.onCreate();

        // Then both timestamps are stamped
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotNull();
    }

    @Test
    void onUpdate_refreshesUpdatedAt() {
        // Given an entity with no updatedAt
        UserNotificationPreferenceSqlEntity entity = new UserNotificationPreferenceSqlEntity();
        assertThat(entity.getUpdatedAt()).isNull();

        // When the @PreUpdate hook runs
        entity.onUpdate();

        // Then updatedAt is set
        assertThat(entity.getUpdatedAt()).isNotNull();
    }
}
