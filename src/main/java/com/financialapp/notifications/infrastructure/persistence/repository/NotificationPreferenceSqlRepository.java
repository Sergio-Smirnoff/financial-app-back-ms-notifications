package com.financialapp.notifications.infrastructure.persistence.repository;

import com.financialapp.notifications.domain.model.notification.NotificationCategory;
import com.financialapp.notifications.infrastructure.persistence.entity.NotificationPreferenceSqlEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationPreferenceSqlRepository extends JpaRepository<NotificationPreferenceSqlEntity, Long> {
    Optional<NotificationPreferenceSqlEntity> findByUserIdAndCategory(Long userId, NotificationCategory category);

    List<NotificationPreferenceSqlEntity> findByUserId(Long userId);
}
