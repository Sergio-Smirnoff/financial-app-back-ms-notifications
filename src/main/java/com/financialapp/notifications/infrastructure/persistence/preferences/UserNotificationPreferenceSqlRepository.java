package com.financialapp.notifications.infrastructure.persistence.preferences;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserNotificationPreferenceSqlRepository extends JpaRepository<UserNotificationPreferenceSqlEntity, Long> {

    Optional<UserNotificationPreferenceSqlEntity> findByUserId(Long userId);

    Page<UserNotificationPreferenceSqlEntity> findByMonthlyEmailEnabledTrue(Pageable pageable);

}
