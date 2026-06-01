package com.financialapp.notifications.infrastructure.repository.preferences;

import com.financialapp.notifications.domain.model.entity.UserNotificationPreference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserNotificationPreferenceSqlRepository extends JpaRepository<UserNotificationPreferenceSqlEntity, Long> {

    Optional<UserNotificationPreferenceSqlEntity> findByUserId(Long userId);

    Page<UserNotificationPreferenceSqlEntity> findByMonthlyEmailEnabledTrue(Pageable pageable);

}
