package com.financialapp.notifications.infrastructure.persistence.repository;

import com.financialapp.notifications.domain.model.notification.DeliveryStatus;
import com.financialapp.notifications.infrastructure.persistence.entity.NotificationDeliverySqlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationDeliverySqlRepository extends JpaRepository<NotificationDeliverySqlEntity, Long> {

    @Query("SELECT d FROM NotificationDeliverySqlEntity d WHERE d.status = :status AND d.nextRetryAt <= :now")
    List<NotificationDeliverySqlEntity> findByStatusAndNextRetryAtBefore(
            @Param("status") DeliveryStatus status,
            @Param("now") LocalDateTime now);
}
