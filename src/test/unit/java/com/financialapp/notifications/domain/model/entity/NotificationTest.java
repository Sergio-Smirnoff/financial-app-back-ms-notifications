package com.financialapp.notifications.domain.model.entity;

import com.financialapp.notifications.domain.model.entity.enums.NotificationChannel;
import com.financialapp.notifications.domain.model.entity.enums.NotificationType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationTest {

    @Test
    void create_buildsUnreadNotificationWithGivenFields() {
        // Given notification data / When created via the factory
        Notification notification = Notification.create(7L, NotificationType.PAYMENT_DUE,
                "title", "message", NotificationChannel.BOTH, "{\"k\":1}");

        // Then the fields are populated and it is unread, no id/timestamp yet
        assertThat(notification.userId()).isEqualTo(7L);
        assertThat(notification.type()).isEqualTo(NotificationType.PAYMENT_DUE);
        assertThat(notification.title()).isEqualTo("title");
        assertThat(notification.message()).isEqualTo("message");
        assertThat(notification.channel()).isEqualTo(NotificationChannel.BOTH);
        assertThat(notification.metadata()).isEqualTo("{\"k\":1}");
        assertThat(notification.read()).isFalse();
        assertThat(notification.id()).isNull();
        assertThat(notification.createdAt()).isNull();
    }

    @Test
    void markAsRead_flipsReadKeepingEverythingElse() {
        // Given an unread notification
        LocalDateTime created = LocalDateTime.of(2026, 1, 1, 0, 0);
        Notification original = Notification.builder()
                .id(1L).userId(2L).type(NotificationType.LOAN_REMINDER).title("t").message("m")
                .channel(NotificationChannel.IN_APP).read(false).metadata("meta").createdAt(created)
                .build();

        // When marking it as read
        Notification read = original.markAsRead();

        // Then only read flips; identity fields are preserved
        assertThat(read.read()).isTrue();
        assertThat(read.id()).isEqualTo(1L);
        assertThat(read.userId()).isEqualTo(2L);
        assertThat(read.type()).isEqualTo(NotificationType.LOAN_REMINDER);
        assertThat(read.title()).isEqualTo("t");
        assertThat(read.message()).isEqualTo("m");
        assertThat(read.channel()).isEqualTo(NotificationChannel.IN_APP);
        assertThat(read.metadata()).isEqualTo("meta");
        assertThat(read.createdAt()).isEqualTo(created);
    }

    @Test
    void equalsHashCodeToString_distinguishInstances() {
        // Given two equal and one different notification
        Notification a = Notification.builder().id(1L).userId(2L).title("t").build();
        Notification b = Notification.builder().id(1L).userId(2L).title("t").build();
        Notification c = Notification.builder().id(9L).userId(2L).title("t").build();

        // Then equality and toString behave as a value record
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b).isNotEqualTo(c);
        assertThat(a.toString()).contains("Notification");
    }
}
