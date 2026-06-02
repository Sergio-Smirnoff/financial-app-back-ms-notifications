package com.financialapp.notifications.infrastructure.repository.preferences;

import com.financialapp.notifications.domain.repository.UserNotificationPreferenceRepository;
import com.financialapp.notifications.domain.model.notification.UserNotificationPreference;
import com.financialapp.notifications.domain.model.pagination.PageResult;
import com.financialapp.notifications.infrastructure.repository.preferences.mapper.UserNotificationPreferenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SqlUserNotificationPreferencePersistence implements UserNotificationPreferenceRepository {

    private final UserNotificationPreferenceSqlRepository sqlRepository;

    @Override
    public Optional<UserNotificationPreference> findByUserId(Long userId) {
        return sqlRepository.findByUserId(userId).map(UserNotificationPreferenceMapper::toDomain);
    }

    @Override
    public PageResult<UserNotificationPreference> findByMonthlyEmailEnabledTrue(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserNotificationPreferenceSqlEntity> springPage = sqlRepository.findByMonthlyEmailEnabledTrue(pageable);
        return new PageResult<>(
                springPage.getContent().stream()
                        .map(UserNotificationPreferenceMapper::toDomain)
                        .collect(Collectors.toList()),
                springPage.getNumber(),
                springPage.getSize(),
                springPage.getTotalElements(),
                springPage.getTotalPages());
    }

    @Override
    @Transactional
    public UserNotificationPreference save(UserNotificationPreference preference) {
        var entity = UserNotificationPreferenceMapper.toEntity(preference);
        var saved = sqlRepository.save(entity);
        return UserNotificationPreferenceMapper.toDomain(saved);
    }

}
