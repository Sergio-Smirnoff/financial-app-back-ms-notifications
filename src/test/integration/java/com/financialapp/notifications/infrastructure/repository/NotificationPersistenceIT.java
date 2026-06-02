package com.financialapp.notifications.infrastructure.repository;

import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import com.financialapp.notifications.domain.model.pagination.PageResult;
import com.financialapp.notifications.domain.repository.NotificationRepository;
import com.financialapp.notifications.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/** Round-trips notifications through the real SqlNotificationPersistence + mapper + H2 entity. */
class NotificationPersistenceIT extends IntegrationTestBase {

    @Autowired NotificationRepository repository;

    private Notification newNotification(Long userId, boolean read) {
        return new Notification(null, userId, NotificationType.PAYMENT_DUE,
                "t", "m", NotificationChannel.IN_APP, read, null, null);
    }

    @Test
    void save_assignsIdAndStampsCreatedAt_andFindByIdReturnsIt() {
        // Given a new notification / When saved
        Notification saved = repository.save(newNotification(101L, false));

        // Then it gets an id and a createdAt, and is retrievable
        assertThat(saved.id()).isNotNull();
        assertThat(saved.createdAt()).isNotNull();
        Optional<Notification> found = repository.findById(saved.id());
        assertThat(found).isPresent();
        assertThat(found.get().userId()).isEqualTo(101L);
    }

    @Test
    void findByUserIdOrderByCreatedAtDesc_paginates() {
        // Given two notifications for a user
        repository.save(newNotification(102L, false));
        repository.save(newNotification(102L, false));

        // When fetching the first page
        PageResult<Notification> page = repository.findByUserIdOrderByCreatedAtDesc(102L, 0, 10);

        // Then both are returned with page metadata
        assertThat(page.content()).hasSize(2);
        assertThat(page.totalElements()).isEqualTo(2);
        assertThat(page.pageNumber()).isZero();
    }

    @Test
    void findTop5_andCountUnread_reflectStoredState() {
        // Given an unread and a read notification for a user
        repository.save(newNotification(103L, false));
        repository.save(newNotification(103L, true));

        // When querying latest and unread count
        List<Notification> latest = repository.findTop5ByUserIdOrderByCreatedAtDesc(103L);
        long unread = repository.countByUserIdAndReadFalse(103L);

        // Then latest lists both and unread counts only the unread one
        assertThat(latest).hasSize(2);
        assertThat(unread).isEqualTo(1);
    }

    @Test
    void markAllAsRead_clearsUnreadCount() {
        // Given two unread notifications for a user
        repository.save(newNotification(104L, false));
        repository.save(newNotification(104L, false));

        // When marking all as read
        int updated = repository.markAllAsRead(104L);

        // Then both are marked and the unread count is zero
        assertThat(updated).isEqualTo(2);
        assertThat(repository.countByUserIdAndReadFalse(104L)).isZero();
    }

    @Test
    void deleteOldNotifications_removesOlderThanThreshold() {
        // Given a saved notification / When deleting everything created before tomorrow
        repository.save(newNotification(105L, false));
        int deleted = repository.deleteOldNotifications(java.time.LocalDateTime.now().plusDays(1));

        // Then at least the saved one is removed
        assertThat(deleted).isGreaterThanOrEqualTo(1);
    }
}
