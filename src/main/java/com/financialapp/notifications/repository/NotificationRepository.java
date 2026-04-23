package com.financialapp.notifications.repository;

import com.financialapp.notifications.model.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<Notification> findTop5ByUserIdOrderByCreatedAtDesc(Long userId);

    long countByUserIdAndReadFalse(Long userId);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.userId = :userId AND n.read = false")
    int markAllAsRead(@Param("userId") Long userId);

    @Query(value = "SELECT * FROM notifications.notifications n " +
                   "WHERE n.user_id = :userId " +
                   "AND n.metadata->>'bankId' = :bankId " +
                   "ORDER BY n.created_at DESC LIMIT 10", nativeQuery = true)
    List<Notification> findLatestByBank(@Param("userId") Long userId, @Param("bankId") String bankId);
}
