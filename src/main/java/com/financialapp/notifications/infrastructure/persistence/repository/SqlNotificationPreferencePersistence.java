package com.financialapp.notifications.infrastructure.persistence.repository;

import com.financialapp.notifications.domain.model.notification.NotificationCategory;
import com.financialapp.notifications.domain.model.notification.NotificationPreference;
import com.financialapp.notifications.domain.repository.NotificationPreferenceRepository;
import com.financialapp.notifications.infrastructure.persistence.mapper.NotificationPreferenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SqlNotificationPreferencePersistence implements NotificationPreferenceRepository {

    private final NotificationPreferenceSqlRepository sqlRepository;

    @Override
    public Optional<NotificationPreference> findByUserIdAndCategory(Long userId, NotificationCategory category) {
        return sqlRepository.findByUserIdAndCategory(userId, category)
                .map(NotificationPreferenceMapper::toDomain);
    }

    @Override
    public List<NotificationPreference> findByUserId(Long userId) {
        return sqlRepository.findByUserId(userId).stream()
                .map(NotificationPreferenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public NotificationPreference save(NotificationPreference preference) {
        var existingOpt = sqlRepository.findByUserIdAndCategory(preference.userId(), preference.category());
        var entity = NotificationPreferenceMapper.toEntity(preference);
        if (existingOpt.isPresent()) {
            entity.setId(existingOpt.get().getId());
        }
        var saved = sqlRepository.save(entity);
        return NotificationPreferenceMapper.toDomain(saved);
    }
}
