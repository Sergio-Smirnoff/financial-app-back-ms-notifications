package com.financialapp.notifications.domain.repository;

import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.pagination.PageResult;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {

    Notification save(Notification notification);

    Optional<Notification> findById(Long id);

    PageResult<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, int page, int size);

    List<Notification> findTop5ByUserIdOrderByCreatedAtDesc(Long userId);

    long countByUserIdAndReadFalse(Long userId);

    int markAllAsRead(Long userId);

    List<Notification> findLatestByBank(Long userId, String bankId);

    int deleteOldNotifications(java.time.LocalDateTime thresholdDate);
}
