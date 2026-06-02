package com.financialapp.notifications.infrastructure.repository.notifications;

import com.financialapp.notifications.domain.model.entity.Notification;
import com.financialapp.notifications.domain.model.entity.enums.NotificationChannel;
import com.financialapp.notifications.domain.model.entity.enums.NotificationType;
import com.financialapp.notifications.domain.model.response.PageResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SqlNotificationPersistenceTest {

    @Mock NotificationSqlRepository sqlRepository;
    @InjectMocks SqlNotificationPersistence persistence;

    private NotificationSqlEntity entity(Long id) {
        return NotificationSqlEntity.builder().id(id).userId(7L).type(NotificationType.PAYMENT_DUE)
                .title("t").message("m").channel(NotificationChannel.IN_APP).read(false)
                .createdAt(LocalDateTime.of(2026, 1, 1, 0, 0)).build();
    }

    private Notification domain() {
        return Notification.builder().userId(7L).type(NotificationType.PAYMENT_DUE)
                .title("t").message("m").channel(NotificationChannel.IN_APP).build();
    }

    @Test
    void save_mapsToEntityAndBack() {
        // Given the JPA repo echoes a persisted entity
        when(sqlRepository.save(any())).thenReturn(entity(1L));

        // When saving a domain notification
        Notification saved = persistence.save(domain());

        // Then the domain object reflects the persisted id
        assertThat(saved.id()).isEqualTo(1L);
        verify(sqlRepository).save(any(NotificationSqlEntity.class));
    }

    @Test
    void findById_mapsPresentEntity() {
        // Given an entity exists
        when(sqlRepository.findById(1L)).thenReturn(Optional.of(entity(1L)));

        // When finding by id / Then it is mapped to the domain
        assertThat(persistence.findById(1L)).map(Notification::id).contains(1L);
    }

    @Test
    void findByUserId_mapsSpringPageToPageResult() {
        // Given a Spring page of one entity
        Pageable pageable = PageRequest.of(0, 10);
        Page<NotificationSqlEntity> page = new PageImpl<>(List.of(entity(1L)), pageable, 1);
        when(sqlRepository.findByUserIdOrderByCreatedAtDesc(7L, pageable)).thenReturn(page);

        // When querying / Then a PageResult mirrors the metadata
        PageResult<Notification> result = persistence.findByUserIdOrderByCreatedAtDesc(7L, 0, 10);
        assertThat(result.content()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.pageNumber()).isZero();
    }

    @Test
    void findTop5_mapsEntities() {
        // Given the repo returns the latest entities
        when(sqlRepository.findTop5ByUserIdOrderByCreatedAtDesc(7L)).thenReturn(List.of(entity(1L)));

        // When querying / Then they are mapped
        assertThat(persistence.findTop5ByUserIdOrderByCreatedAtDesc(7L)).hasSize(1);
    }

    @Test
    void countUnread_delegates() {
        // Given a count / When querying / Then it is returned
        when(sqlRepository.countByUserIdAndReadFalse(7L)).thenReturn(3L);
        assertThat(persistence.countByUserIdAndReadFalse(7L)).isEqualTo(3L);
    }

    @Test
    void markAllAsRead_delegates() {
        // Given an update count / When marking all / Then it is returned
        when(sqlRepository.markAllAsRead(7L)).thenReturn(2);
        assertThat(persistence.markAllAsRead(7L)).isEqualTo(2);
    }

    @Test
    void findLatestByBank_delegatesAndMaps() {
        // Given the native bank query returns one entity
        when(sqlRepository.findLatestByBank(7L, "55")).thenReturn(List.of(entity(1L)));

        // When querying by bank / Then it delegates with the stringified bankId and maps the result
        assertThat(persistence.findLatestByBank(7L, "55")).hasSize(1);
        verify(sqlRepository).findLatestByBank(7L, "55");
    }

    @Test
    void deleteOldNotifications_delegates() {
        // Given a delete count
        when(sqlRepository.deleteOldNotifications(any())).thenReturn(5);

        // When deleting old / Then it delegates with the threshold and returns the count
        LocalDateTime threshold = LocalDateTime.of(2026, 1, 1, 0, 0);
        assertThat(persistence.deleteOldNotifications(threshold)).isEqualTo(5);
        ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(sqlRepository).deleteOldNotifications(captor.capture());
        assertThat(captor.getValue()).isEqualTo(threshold);
    }
}
