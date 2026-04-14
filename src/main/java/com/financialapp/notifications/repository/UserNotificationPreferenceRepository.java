package com.financialapp.notifications.repository;

import com.financialapp.notifications.model.entity.UserNotificationPreference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserNotificationPreferenceRepository extends JpaRepository<UserNotificationPreference, Long> {

    Optional<UserNotificationPreference> findByUserId(Long userId);

    List<UserNotificationPreference> findByMonthlyEmailEnabledTrue();

    Page<UserNotificationPreference> findByMonthlyEmailEnabledTrue(Pageable pageable);
}
