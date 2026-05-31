package com.financialapp.notifications.domain.repository;

import com.financialapp.notifications.domain.model.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {

    Notification save(Notification notification);

    Optional<Notification> findById(Long id);

    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<Notification> findTop5ByUserIdOrderByCreatedAtDesc(Long userId);

    long countByUserIdAndReadFalse(Long userId);

    int markAllAsRead(Long userId);

    List<Notification> findLatestByBank(Long userId, String bankId);

    int deleteOldNotifications(java.time.LocalDateTime thresholdDate);
}
