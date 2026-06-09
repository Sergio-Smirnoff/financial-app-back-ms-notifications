package com.financialapp.notifications.infrastructure.persistence.repository;

import com.financialapp.notifications.domain.model.notification.DeliveryStatus;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationDelivery;
import com.financialapp.notifications.infrastructure.persistence.entity.NotificationDeliverySqlEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SqlNotificationDeliveryPersistenceTest {

    @Mock NotificationDeliverySqlRepository repository;
    @InjectMocks SqlNotificationDeliveryPersistence persistence;

    private NotificationDeliverySqlEntity entity(Long id, DeliveryStatus status) {
        return NotificationDeliverySqlEntity.builder()
                .id(id).notificationId(5L).channel(NotificationChannel.EMAIL)
                .status(status).attempts(0).build();
    }

    @Test
    void save_mapsToEntityAndBack() {
        when(repository.save(any())).thenReturn(entity(10L, DeliveryStatus.PENDING));

        NotificationDelivery saved = persistence.save(
                NotificationDelivery.pending(5L, NotificationChannel.EMAIL));

        assertThat(saved.id()).isEqualTo(10L);
        verify(repository).save(any(NotificationDeliverySqlEntity.class));
    }

    @Test
    void findFailedReadyToRetry_delegatesAndMaps() {
        LocalDateTime now = LocalDateTime.of(2026, 6, 1, 9, 0);
        when(repository.findByStatusAndNextRetryAtBefore(DeliveryStatus.FAILED, now))
                .thenReturn(List.of(entity(1L, DeliveryStatus.FAILED)));

        List<NotificationDelivery> result = persistence.findFailedReadyToRetry(now);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).status()).isEqualTo(DeliveryStatus.FAILED);
        verify(repository).findByStatusAndNextRetryAtBefore(DeliveryStatus.FAILED, now);
    }

    @Test
    void updateStatus_whenEntityExists_appliesChangesAndSaves() {
        NotificationDeliverySqlEntity existing = entity(7L, DeliveryStatus.PENDING);
        when(repository.findById(7L)).thenReturn(Optional.of(existing));
        LocalDateTime retry = LocalDateTime.of(2026, 6, 1, 9, 5);
        NotificationDelivery update = new NotificationDelivery(
                7L, 5L, NotificationChannel.EMAIL, DeliveryStatus.FAILED, 2, "boom", retry, null, null);

        persistence.updateStatus(update);

        ArgumentCaptor<NotificationDeliverySqlEntity> captor =
                ArgumentCaptor.forClass(NotificationDeliverySqlEntity.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(DeliveryStatus.FAILED);
        assertThat(captor.getValue().getAttempts()).isEqualTo(2);
        assertThat(captor.getValue().getLastError()).isEqualTo("boom");
        assertThat(captor.getValue().getNextRetryAt()).isEqualTo(retry);
    }

    @Test
    void updateStatus_whenEntityMissing_isNoOp() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        NotificationDelivery update = new NotificationDelivery(
                99L, 5L, NotificationChannel.EMAIL, DeliveryStatus.SENT, 1, null, null, null, null);

        persistence.updateStatus(update);

        verify(repository, never()).save(any());
    }
}
