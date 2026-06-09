package com.financialapp.notifications.infrastructure.persistence.mapper;

import com.financialapp.notifications.domain.model.notification.DeliveryStatus;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationDelivery;
import com.financialapp.notifications.infrastructure.persistence.entity.NotificationDeliverySqlEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationDeliveryMapperTest {

    @Test
    void mapperUtility_isInstantiable() {
        assertThat(new NotificationDeliveryMapper()).isNotNull();
    }

    @Test
    void toEntity_copiesEveryField() {
        LocalDateTime retry = LocalDateTime.of(2026, 6, 1, 9, 0);
        NotificationDelivery delivery = new NotificationDelivery(
                10L, 5L, NotificationChannel.EMAIL, DeliveryStatus.FAILED,
                2, "smtp down", retry, null, null);

        NotificationDeliverySqlEntity entity = NotificationDeliveryMapper.toEntity(delivery);

        assertThat(entity.getId()).isEqualTo(10L);
        assertThat(entity.getNotificationId()).isEqualTo(5L);
        assertThat(entity.getChannel()).isEqualTo(NotificationChannel.EMAIL);
        assertThat(entity.getStatus()).isEqualTo(DeliveryStatus.FAILED);
        assertThat(entity.getAttempts()).isEqualTo(2);
        assertThat(entity.getLastError()).isEqualTo("smtp down");
        assertThat(entity.getNextRetryAt()).isEqualTo(retry);
    }

    @Test
    void toDomain_copiesEveryField() {
        LocalDateTime created = LocalDateTime.of(2026, 6, 1, 8, 0);
        LocalDateTime updated = LocalDateTime.of(2026, 6, 1, 8, 30);
        LocalDateTime retry = LocalDateTime.of(2026, 6, 1, 9, 0);
        NotificationDeliverySqlEntity entity = NotificationDeliverySqlEntity.builder()
                .id(11L).notificationId(6L).channel(NotificationChannel.IN_APP)
                .status(DeliveryStatus.SENT).attempts(1).lastError("e").nextRetryAt(retry)
                .createdAt(created).updatedAt(updated).build();

        NotificationDelivery domain = NotificationDeliveryMapper.toDomain(entity);

        assertThat(domain.id()).isEqualTo(11L);
        assertThat(domain.notificationId()).isEqualTo(6L);
        assertThat(domain.channel()).isEqualTo(NotificationChannel.IN_APP);
        assertThat(domain.status()).isEqualTo(DeliveryStatus.SENT);
        assertThat(domain.attempts()).isEqualTo(1);
        assertThat(domain.lastError()).isEqualTo("e");
        assertThat(domain.nextRetryAt()).isEqualTo(retry);
        assertThat(domain.createdAt()).isEqualTo(created);
        assertThat(domain.updatedAt()).isEqualTo(updated);
    }
}
