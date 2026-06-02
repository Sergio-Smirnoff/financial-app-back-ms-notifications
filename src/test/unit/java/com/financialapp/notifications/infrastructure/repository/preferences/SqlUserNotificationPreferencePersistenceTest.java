package com.financialapp.notifications.infrastructure.repository.preferences;

import com.financialapp.notifications.domain.model.notification.UserNotificationPreference;
import com.financialapp.notifications.domain.model.pagination.PageResult;
import com.financialapp.notifications.infrastructure.persistence.entity.UserNotificationPreferenceSqlEntity;
import com.financialapp.notifications.infrastructure.persistence.repository.SqlUserNotificationPreferencePersistence;
import com.financialapp.notifications.infrastructure.persistence.repository.UserNotificationPreferenceSqlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SqlUserNotificationPreferencePersistenceTest {

    @Mock UserNotificationPreferenceSqlRepository sqlRepository;
    @InjectMocks SqlUserNotificationPreferencePersistence persistence;

    private UserNotificationPreferenceSqlEntity entity(Long userId) {
        return UserNotificationPreferenceSqlEntity.builder().id(1L).userId(userId)
                .email("e@x.com").monthlyEmailEnabled(true).build();
    }

    @Test
    void findByUserId_mapsPresentEntity() {
        // Given an entity exists
        when(sqlRepository.findByUserId(7L)).thenReturn(Optional.of(entity(7L)));

        // When finding by user / Then it is mapped to the domain
        assertThat(persistence.findByUserId(7L)).map(UserNotificationPreference::userId).contains(7L);
    }

    @Test
    void findByMonthlyEmailEnabledTrue_mapsSpringPage() {
        // Given a Spring page of one preference
        Pageable pageable = PageRequest.of(0, 50);
        Page<UserNotificationPreferenceSqlEntity> page = new PageImpl<>(List.of(entity(7L)), pageable, 1);
        when(sqlRepository.findByMonthlyEmailEnabledTrue(pageable)).thenReturn(page);

        // When querying enabled preferences / Then a PageResult mirrors the metadata
        PageResult<UserNotificationPreference> result = persistence.findByMonthlyEmailEnabledTrue(0, 50);
        assertThat(result.content()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
    }

    @Test
    void save_mapsToEntityAndBack() {
        // Given the JPA repo echoes the saved entity
        when(sqlRepository.save(any())).thenReturn(entity(7L));

        // When saving a domain preference
        UserNotificationPreference saved = persistence.save(
                new UserNotificationPreference(null, 7L, "e@x.com", true, null, null));

        // Then the domain object reflects the persisted id
        assertThat(saved.id()).isEqualTo(1L);
        verify(sqlRepository).save(any(UserNotificationPreferenceSqlEntity.class));
    }
}
