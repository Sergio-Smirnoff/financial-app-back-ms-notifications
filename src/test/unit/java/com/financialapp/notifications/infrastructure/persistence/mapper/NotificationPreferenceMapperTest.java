package com.financialapp.notifications.infrastructure.persistence.mapper;

import com.financialapp.notifications.domain.model.notification.NotificationCategory;
import com.financialapp.notifications.domain.model.notification.NotificationPreference;
import com.financialapp.notifications.infrastructure.persistence.entity.NotificationPreferenceSqlEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationPreferenceMapperTest {

    @Test
    void mapperUtility_isInstantiable() {
        assertThat(new NotificationPreferenceMapper()).isNotNull();
    }

    @Test
    void toDomain_copiesEveryField() {
        // Given a persisted entity
        LocalDateTime created = LocalDateTime.of(2026, 8, 1, 10, 0);
        LocalDateTime updated = LocalDateTime.of(2026, 8, 2, 11, 0);
        NotificationPreferenceSqlEntity entity = NotificationPreferenceSqlEntity.builder()
                .id(4L).userId(7L).category(NotificationCategory.BUDGET)
                .inAppEnabled(false).emailEnabled(true)
                .createdAt(created).updatedAt(updated).build();

        // When mapped to the domain model
        NotificationPreference domain = NotificationPreferenceMapper.toDomain(entity);

        // Then every field is carried over
        assertThat(domain.id()).isEqualTo(4L);
        assertThat(domain.userId()).isEqualTo(7L);
        assertThat(domain.category()).isEqualTo(NotificationCategory.BUDGET);
        assertThat(domain.inAppEnabled()).isFalse();
        assertThat(domain.emailEnabled()).isTrue();
        assertThat(domain.createdAt()).isEqualTo(created);
        assertThat(domain.updatedAt()).isEqualTo(updated);
    }

    @Test
    void toDomain_whenEntityIsNull_returnsNull() {
        // Given no entity / When mapped / Then the result is null
        assertThat(NotificationPreferenceMapper.toDomain(null)).isNull();
    }

    @Test
    void toEntity_copiesEveryField() {
        // Given a domain preference
        LocalDateTime created = LocalDateTime.of(2026, 8, 3, 12, 0);
        LocalDateTime updated = LocalDateTime.of(2026, 8, 4, 13, 0);
        NotificationPreference domain = new NotificationPreference(
                9L, 3L, NotificationCategory.SUMMARY, true, true, created, updated);

        // When mapped to the entity
        NotificationPreferenceSqlEntity entity = NotificationPreferenceMapper.toEntity(domain);

        // Then every field is carried over
        assertThat(entity.getId()).isEqualTo(9L);
        assertThat(entity.getUserId()).isEqualTo(3L);
        assertThat(entity.getCategory()).isEqualTo(NotificationCategory.SUMMARY);
        assertThat(entity.isInAppEnabled()).isTrue();
        assertThat(entity.isEmailEnabled()).isTrue();
        assertThat(entity.getCreatedAt()).isEqualTo(created);
        assertThat(entity.getUpdatedAt()).isEqualTo(updated);
    }

    @Test
    void toEntity_whenDomainIsNull_returnsNull() {
        // Given no domain preference / When mapped / Then the result is null
        assertThat(NotificationPreferenceMapper.toEntity(null)).isNull();
    }
}
