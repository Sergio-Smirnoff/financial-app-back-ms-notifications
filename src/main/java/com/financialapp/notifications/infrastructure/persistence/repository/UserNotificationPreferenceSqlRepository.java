package com.financialapp.notifications.infrastructure.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.financialapp.notifications.infrastructure.persistence.entity.UserNotificationPreferenceSqlEntity;

import java.util.Optional;

public interface UserNotificationPreferenceSqlRepository
        extends JpaRepository<UserNotificationPreferenceSqlEntity, Long> {

    Optional<UserNotificationPreferenceSqlEntity> findByUserId(Long userId);

    Page<UserNotificationPreferenceSqlEntity> findByMonthlyEmailEnabledTrue(Pageable pageable);

}
