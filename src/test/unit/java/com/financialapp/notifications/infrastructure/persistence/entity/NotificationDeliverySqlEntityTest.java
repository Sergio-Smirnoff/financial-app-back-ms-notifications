package com.financialapp.notifications.infrastructure.persistence.entity;

import com.financialapp.notifications.domain.model.notification.DeliveryStatus;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationDeliverySqlEntityTest {

    @Test
    void builderAndAccessors_roundTripFields() {
        LocalDateTime retry = LocalDateTime.of(2026, 6, 1, 9, 0);
        NotificationDeliverySqlEntity entity = NotificationDeliverySqlEntity.builder()
                .id(1L).notificationId(2L).channel(NotificationChannel.EMAIL)
                .status(DeliveryStatus.FAILED).attempts(3).lastError("boom").nextRetryAt(retry)
                .createdAt(LocalDateTime.of(2026, 1, 1, 0, 0))
                .updatedAt(LocalDateTime.of(2026, 1, 2, 0, 0))
                .build();

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getNotificationId()).isEqualTo(2L);
        assertThat(entity.getChannel()).isEqualTo(NotificationChannel.EMAIL);
        assertThat(entity.getStatus()).isEqualTo(DeliveryStatus.FAILED);
        assertThat(entity.getAttempts()).isEqualTo(3);
        assertThat(entity.getLastError()).isEqualTo("boom");
        assertThat(entity.getNextRetryAt()).isEqualTo(retry);
        assertThat(entity.getCreatedAt()).isEqualTo(LocalDateTime.of(2026, 1, 1, 0, 0));
        assertThat(entity.getUpdatedAt()).isEqualTo(LocalDateTime.of(2026, 1, 2, 0, 0));
    }

    @Test
    void setters_mutateEveryField() {
        NotificationDeliverySqlEntity entity = new NotificationDeliverySqlEntity();
        LocalDateTime now = LocalDateTime.of(2026, 6, 1, 9, 0);

        entity.setId(9L);
        entity.setNotificationId(8L);
        entity.setChannel(NotificationChannel.BOTH);
        entity.setStatus(DeliveryStatus.SENT);
        entity.setAttempts(1);
        entity.setLastError("err");
        entity.setNextRetryAt(now);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        assertThat(entity.getId()).isEqualTo(9L);
        assertThat(entity.getNotificationId()).isEqualTo(8L);
        assertThat(entity.getChannel()).isEqualTo(NotificationChannel.BOTH);
        assertThat(entity.getStatus()).isEqualTo(DeliveryStatus.SENT);
        assertThat(entity.getAttempts()).isEqualTo(1);
        assertThat(entity.getLastError()).isEqualTo("err");
        assertThat(entity.getNextRetryAt()).isEqualTo(now);
        assertThat(entity.getCreatedAt()).isEqualTo(now);
        assertThat(entity.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void allArgsConstructor_buildsFullyPopulatedEntity() {
        LocalDateTime now = LocalDateTime.of(2026, 6, 1, 9, 0);
        NotificationDeliverySqlEntity entity = new NotificationDeliverySqlEntity(
                1L, 2L, NotificationChannel.IN_APP, DeliveryStatus.PENDING, 0, null, null, now, now);

        assertThat(entity.getChannel()).isEqualTo(NotificationChannel.IN_APP);
        assertThat(entity.getStatus()).isEqualTo(DeliveryStatus.PENDING);
    }

    @Test
    void onCreate_stampsCreatedAndUpdated() {
        NotificationDeliverySqlEntity entity = new NotificationDeliverySqlEntity();

        entity.onCreate();

        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotNull();
    }

    @Test
    void onUpdate_refreshesUpdatedAt() {
        NotificationDeliverySqlEntity entity = new NotificationDeliverySqlEntity();

        entity.onUpdate();

        assertThat(entity.getUpdatedAt()).isNotNull();
    }
}
