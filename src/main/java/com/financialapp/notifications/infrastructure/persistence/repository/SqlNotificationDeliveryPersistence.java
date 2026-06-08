package com.financialapp.notifications.infrastructure.persistence.repository;

import com.financialapp.notifications.domain.model.notification.DeliveryStatus;
import com.financialapp.notifications.domain.model.notification.NotificationDelivery;
import com.financialapp.notifications.domain.repository.NotificationDeliveryRepository;
import com.financialapp.notifications.infrastructure.persistence.entity.NotificationDeliverySqlEntity;
import com.financialapp.notifications.infrastructure.persistence.mapper.NotificationDeliveryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SqlNotificationDeliveryPersistence implements NotificationDeliveryRepository {

    private final NotificationDeliverySqlRepository repository;

    @Override
    public NotificationDelivery save(NotificationDelivery delivery) {
        NotificationDeliverySqlEntity entity = NotificationDeliveryMapper.toEntity(delivery);
        return NotificationDeliveryMapper.toDomain(repository.save(entity));
    }

    @Override
    public List<NotificationDelivery> findFailedReadyToRetry(LocalDateTime now) {
        return repository.findByStatusAndNextRetryAtBefore(DeliveryStatus.FAILED, now)
                .stream()
                .map(NotificationDeliveryMapper::toDomain)
                .toList();
    }

    @Override
    public void updateStatus(NotificationDelivery delivery) {
        repository.findById(delivery.id()).ifPresent(entity -> {
            entity.setStatus(delivery.status());
            entity.setAttempts(delivery.attempts());
            entity.setLastError(delivery.lastError());
            entity.setNextRetryAt(delivery.nextRetryAt());
            repository.save(entity);
        });
    }
}
