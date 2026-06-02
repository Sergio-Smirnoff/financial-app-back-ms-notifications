package com.financialapp.notifications.web.controller.mapper;

import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import com.financialapp.notifications.web.controller.dto.NotificationResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/** Covers the generated MapStruct NotificationMapperImpl, including its null-guard branch. */
class NotificationMapperImplTest {

    private final NotificationMapper mapper = new NotificationMapperImpl();

    @Test
    void toResponse_null_returnsNull() {
        // Given a null notification / When mapped / Then the null-guard branch returns null
        assertThat(mapper.toResponse(null)).isNull();
    }

    @Test
    void toResponse_populated_mapsEnumNamesAndFields() {
        // Given a fully populated notification
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        Notification notification = new Notification(1L, 2L, NotificationType.PAYMENT_DUE,
                "t", "m", NotificationChannel.BOTH, true, "meta", now);

        // When mapped
        NotificationResponse response = mapper.toResponse(notification);

        // Then every field and the enum names are mapped
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUserId()).isEqualTo(2L);
        assertThat(response.getType()).isEqualTo("PAYMENT_DUE");
        assertThat(response.getChannel()).isEqualTo("BOTH");
        assertThat(response.getTitle()).isEqualTo("t");
        assertThat(response.getMessage()).isEqualTo("m");
        assertThat(response.isRead()).isTrue();
        assertThat(response.getMetadata()).isEqualTo("meta");
        assertThat(response.getCreatedAt()).isEqualTo(now);
    }
}
