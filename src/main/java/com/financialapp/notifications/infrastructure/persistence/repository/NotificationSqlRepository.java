package com.financialapp.notifications.infrastructure.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.financialapp.notifications.infrastructure.persistence.entity.NotificationSqlEntity;

import java.util.List;

public interface NotificationSqlRepository extends JpaRepository<NotificationSqlEntity, Long> {

    Page<NotificationSqlEntity> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<NotificationSqlEntity> findTop5ByUserIdOrderByCreatedAtDesc(Long userId);

    long countByUserIdAndReadFalse(Long userId);

    @Modifying
    @Query("UPDATE NotificationSqlEntity n SET n.read = true WHERE n.userId = :userId AND n.read = false")
    int markAllAsRead(@Param("userId") Long userId);

    @Query(value = "SELECT * FROM notifications.notifications n " +
            "WHERE n.user_id = :userId " +
            "AND n.metadata->>'bankId' = :bankId " +
            "ORDER BY n.created_at DESC LIMIT 10", nativeQuery = true)
    List<NotificationSqlEntity> findLatestByBank(@Param("userId") Long userId, @Param("bankId") String bankId);

    @Modifying
    @Query("DELETE FROM NotificationSqlEntity n WHERE n.createdAt < :thresholdDate")
    int deleteOldNotifications(@Param("thresholdDate") java.time.LocalDateTime thresholdDate);
}
