package com.financialapp.notifications.infrastructure.persistence.repository;

import com.financialapp.notifications.domain.model.notification.NotificationCategory;
import com.financialapp.notifications.domain.model.notification.NotificationPreference;
import com.financialapp.notifications.infrastructure.persistence.entity.NotificationPreferenceSqlEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SqlNotificationPreferencePersistenceTest {

    @Mock NotificationPreferenceSqlRepository sqlRepository;
    @InjectMocks SqlNotificationPreferencePersistence persistence;

    private NotificationPreferenceSqlEntity entity(Long id, NotificationCategory category) {
        return NotificationPreferenceSqlEntity.builder()
                .id(id).userId(7L).category(category)
                .inAppEnabled(true).emailEnabled(false).build();
    }

    @Test
    void findByUserIdAndCategory_delegatesAndMaps() {
        // Given a stored preference for the category
        when(sqlRepository.findByUserIdAndCategory(7L, NotificationCategory.BUDGET))
                .thenReturn(Optional.of(entity(3L, NotificationCategory.BUDGET)));

        // When looked up
        Optional<NotificationPreference> result =
                persistence.findByUserIdAndCategory(7L, NotificationCategory.BUDGET);

        // Then the domain model is returned
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(3L);
        assertThat(result.get().category()).isEqualTo(NotificationCategory.BUDGET);
    }

    @Test
    void findByUserId_delegatesAndMapsEveryRow() {
        // Given two stored preferences for the user
        when(sqlRepository.findByUserId(7L)).thenReturn(List.of(
                entity(1L, NotificationCategory.BUDGET),
                entity(2L, NotificationCategory.SUMMARY)));

        // When listed
        List<NotificationPreference> result = persistence.findByUserId(7L);

        // Then every row is mapped to the domain model
        assertThat(result).hasSize(2);
        assertThat(result.get(0).category()).isEqualTo(NotificationCategory.BUDGET);
        assertThat(result.get(1).category()).isEqualTo(NotificationCategory.SUMMARY);
        verify(sqlRepository).findByUserId(7L);
    }

    @Test
    void save_whenNoRowExists_insertsWithoutId() {
        // Given no stored preference for the category
        when(sqlRepository.findByUserIdAndCategory(7L, NotificationCategory.SUMMARY))
                .thenReturn(Optional.empty());
        when(sqlRepository.save(any())).thenReturn(entity(20L, NotificationCategory.SUMMARY));

        // When saving a fresh preference
        NotificationPreference saved = persistence.save(
                NotificationPreference.defaults(7L, NotificationCategory.SUMMARY));

        // Then the entity is persisted with no id and the generated id comes back
        ArgumentCaptor<NotificationPreferenceSqlEntity> captor =
                ArgumentCaptor.forClass(NotificationPreferenceSqlEntity.class);
        verify(sqlRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isNull();
        assertThat(saved.id()).isEqualTo(20L);
    }

    @Test
    void save_whenRowExists_reusesExistingId() {
        // Given a stored preference for the same user and category
        when(sqlRepository.findByUserIdAndCategory(7L, NotificationCategory.SUMMARY))
                .thenReturn(Optional.of(entity(42L, NotificationCategory.SUMMARY)));
        when(sqlRepository.save(any())).thenReturn(entity(42L, NotificationCategory.SUMMARY));

        // When saving an id-less preference for that pair
        NotificationPreference saved = persistence.save(
                NotificationPreference.defaults(7L, NotificationCategory.SUMMARY));

        // Then the existing id is stamped on the entity so the write is an update
        ArgumentCaptor<NotificationPreferenceSqlEntity> captor =
                ArgumentCaptor.forClass(NotificationPreferenceSqlEntity.class);
        verify(sqlRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(42L);
        assertThat(saved.id()).isEqualTo(42L);
    }
}
