package com.financialapp.notifications.domain.repository;

import com.financialapp.notifications.domain.model.notification.DeliveryStatus;
import com.financialapp.notifications.domain.model.notification.NotificationDelivery;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationDeliveryRepository {
    NotificationDelivery save(NotificationDelivery delivery);
    List<NotificationDelivery> findFailedReadyToRetry(LocalDateTime now);
    void updateStatus(NotificationDelivery delivery);
}
