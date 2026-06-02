package com.financialapp.notifications.infrastructure.persistence.repository;

import com.financialapp.notifications.domain.repository.NotificationRepository;
import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.pagination.PageResult;
import com.financialapp.notifications.infrastructure.persistence.entity.NotificationSqlEntity;
import com.financialapp.notifications.infrastructure.persistence.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SqlNotificationPersistence implements NotificationRepository {

    private final NotificationSqlRepository sqlRepository;

    @Override
    @Transactional
    public Notification save(Notification notification) {
        NotificationSqlEntity entity = NotificationMapper.toEntity(notification);
        NotificationSqlEntity saved = sqlRepository.save(entity);
        return NotificationMapper.toDomain(saved);
    }

    @Override
    public Optional<Notification> findById(Long id) {
        return sqlRepository.findById(id).map(NotificationMapper::toDomain);
    }

    @Override
    public PageResult<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationSqlEntity> springPage = sqlRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return new PageResult<>(
                springPage.getContent().stream()
                        .map(NotificationMapper::toDomain)
                        .collect(Collectors.toList()),
                springPage.getNumber(),
                springPage.getSize(),
                springPage.getTotalElements(),
                springPage.getTotalPages());
    }

    @Override
    public List<Notification> findTop5ByUserIdOrderByCreatedAtDesc(Long userId) {
        return sqlRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(NotificationMapper::toDomain)
                .toList();
    }

    @Override
    public long countByUserIdAndReadFalse(Long userId) {
        return sqlRepository.countByUserIdAndReadFalse(userId);
    }

    @Override
    @Transactional
    public int markAllAsRead(Long userId) {
        return sqlRepository.markAllAsRead(userId);
    }

    @Override
    public List<Notification> findLatestByBank(Long userId, String bankId) {
        return sqlRepository.findLatestByBank(userId, bankId).stream()
                .map(NotificationMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public int deleteOldNotifications(java.time.LocalDateTime thresholdDate) {
        return sqlRepository.deleteOldNotifications(thresholdDate);
    }
}
