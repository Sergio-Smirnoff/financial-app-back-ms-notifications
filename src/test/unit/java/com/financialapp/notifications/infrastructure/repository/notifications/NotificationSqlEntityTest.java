package com.financialapp.notifications.infrastructure.repository.notifications;

import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import com.financialapp.notifications.infrastructure.persistence.entity.NotificationSqlEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationSqlEntityTest {

    @Test
    void builderAndAccessors_roundTripFields() {
        // Given / When built
        NotificationSqlEntity entity = NotificationSqlEntity.builder()
                .id(1L).userId(2L).type(NotificationType.PAYMENT_DUE).title("t").message("m")
                .channel(NotificationChannel.BOTH).read(true).metadata("meta").build();

        // Then accessors expose the fields
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getUserId()).isEqualTo(2L);
        assertThat(entity.getType()).isEqualTo(NotificationType.PAYMENT_DUE);
        assertThat(entity.getTitle()).isEqualTo("t");
        assertThat(entity.getMessage()).isEqualTo("m");
        assertThat(entity.getChannel()).isEqualTo(NotificationChannel.BOTH);
        assertThat(entity.isRead()).isTrue();
        assertThat(entity.getMetadata()).isEqualTo("meta");
    }

    @Test
    void onCreate_stampsCreatedAt() {
        // Given a fresh entity with no createdAt
        NotificationSqlEntity entity = new NotificationSqlEntity();
        assertThat(entity.getCreatedAt()).isNull();

        // When the @PrePersist hook runs
        entity.onCreate();

        // Then createdAt is stamped
        assertThat(entity.getCreatedAt()).isNotNull();
    }
}
