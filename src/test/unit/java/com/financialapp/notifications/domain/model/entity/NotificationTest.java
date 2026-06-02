package com.financialapp.notifications.domain.model.entity;

import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationType;
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
        Notification original = new Notification(1L, 2L, NotificationType.LOAN_REMINDER,
                "t", "m", NotificationChannel.IN_APP, false, "meta", created);

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
        Notification a = new Notification(1L, 2L, null, "t", null, null, false, null, null);
        Notification b = new Notification(1L, 2L, null, "t", null, null, false, null, null);
        Notification c = new Notification(9L, 2L, null, "t", null, null, false, null, null);

        // Then equality and toString behave as a value record
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b).isNotEqualTo(c);
        assertThat(a.toString()).contains("Notification");
    }
}
