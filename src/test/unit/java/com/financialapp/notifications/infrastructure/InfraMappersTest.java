package com.financialapp.notifications.infrastructure;

import com.financialapp.notifications.domain.model.entity.Notification;
import com.financialapp.notifications.domain.model.entity.UserNotificationPreference;
import com.financialapp.notifications.domain.model.entity.enums.NotificationChannel;
import com.financialapp.notifications.domain.model.entity.enums.NotificationType;
import com.financialapp.notifications.domain.model.entity.summary.CategorySummary;
import com.financialapp.notifications.infrastructure.client.dto.CategorySummaryResponse;
import com.financialapp.notifications.infrastructure.client.mapper.CategorySummaryMapper;
import com.financialapp.notifications.infrastructure.repository.notifications.NotificationSqlEntity;
import com.financialapp.notifications.infrastructure.repository.notifications.mapper.NotificationMapper;
import com.financialapp.notifications.infrastructure.repository.preferences.UserNotificationPreferenceSqlEntity;
import com.financialapp.notifications.infrastructure.repository.preferences.mapper.UserNotificationPreferenceMapper;
import com.financialapp.notifications.infrastructure.sse.dto.SseNotificationEntity;
import com.financialapp.notifications.infrastructure.sse.mapper.SseNotificationMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class InfraMappersTest {

    @Test
    void categorySummaryMapper_mapsResponseToDomain() {
        // Given a client DTO / When mapped (and the utility class is instantiable)
        new CategorySummaryMapper();
        CategorySummary domain = CategorySummaryMapper.toDomain(CategorySummaryResponse.builder()
                .categoryName("Food").subcategoryName("Cafe").totalAmount(new BigDecimal("12.34"))
                .currency("ARS").transactionCount(3L).build());

        // Then all fields are copied
        assertThat(domain.categoryName()).isEqualTo("Food");
        assertThat(domain.subcategoryName()).isEqualTo("Cafe");
        assertThat(domain.totalAmount()).isEqualByComparingTo("12.34");
        assertThat(domain.currency()).isEqualTo("ARS");
        assertThat(domain.transactionCount()).isEqualTo(3L);
    }

    @Test
    void categorySummaryResponse_buildsAndExposesFields() {
        // Given / When built / Then accessors expose fields
        CategorySummaryResponse r = CategorySummaryResponse.builder().categoryName("Food")
                .subcategoryName("Cafe").totalAmount(new BigDecimal("1")).currency("ARS").transactionCount(1L).build();
        assertThat(r.getCategoryName()).isEqualTo("Food");
        assertThat(r.getSubcategoryName()).isEqualTo("Cafe");
        assertThat(r.getTotalAmount()).isEqualByComparingTo("1");
        assertThat(r.getCurrency()).isEqualTo("ARS");
        assertThat(r.getTransactionCount()).isEqualTo(1L);
    }

    @Test
    void notificationMapper_roundTripsDomainAndEntity() {
        // Given a domain notification / When mapped to entity and back (utility instantiable)
        new NotificationMapper();
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        Notification domain = Notification.builder().id(1L).userId(2L).type(NotificationType.PAYMENT_DUE)
                .title("t").message("m").channel(NotificationChannel.BOTH).read(true).metadata("meta")
                .createdAt(now).build();

        NotificationSqlEntity entity = NotificationMapper.toEntity(domain);
        Notification back = NotificationMapper.toDomain(entity);

        // Then every field survives the round-trip
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getUserId()).isEqualTo(2L);
        assertThat(entity.getType()).isEqualTo(NotificationType.PAYMENT_DUE);
        assertThat(back).isEqualTo(domain);
    }

    @Test
    void preferenceMapper_roundTripsDomainAndEntity() {
        // Given a domain preference / When mapped to entity and back (utility instantiable)
        new UserNotificationPreferenceMapper();
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        UserNotificationPreference domain = UserNotificationPreference.builder().id(1L).userId(2L)
                .email("e@x.com").monthlyEmailEnabled(false).createdAt(now).updatedAt(now).build();

        UserNotificationPreferenceSqlEntity entity = UserNotificationPreferenceMapper.toEntity(domain);
        UserNotificationPreference back = UserNotificationPreferenceMapper.toDomain(entity);

        // Then it round-trips
        assertThat(entity.getEmail()).isEqualTo("e@x.com");
        assertThat(entity.isMonthlyEmailEnabled()).isFalse();
        assertThat(back).isEqualTo(domain);
    }

    @Test
    void preferenceMapper_returnsNullForNullInputs() {
        // Given null inputs / When mapped / Then the null guard returns null on both directions
        assertThat(UserNotificationPreferenceMapper.toDomain(null)).isNull();
        assertThat(UserNotificationPreferenceMapper.toEntity(null)).isNull();
    }

    @Test
    void sseNotificationMapper_mapsDomainToSseEntity() {
        // Given a domain notification / When mapped (utility instantiable)
        new SseNotificationMapper();
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        Notification domain = Notification.builder().id(1L).userId(2L).type(NotificationType.LOAN_REMINDER)
                .title("t").message("m").channel(NotificationChannel.IN_APP).read(false).metadata("meta")
                .createdAt(now).build();

        SseNotificationEntity sse = SseNotificationMapper.toEntity(domain);

        // Then enum names are stringified and fields copied
        assertThat(sse.id()).isEqualTo(1L);
        assertThat(sse.userId()).isEqualTo(2L);
        assertThat(sse.type()).isEqualTo("LOAN_REMINDER");
        assertThat(sse.channel()).isEqualTo("IN_APP");
        assertThat(sse.title()).isEqualTo("t");
        assertThat(sse.message()).isEqualTo("m");
        assertThat(sse.read()).isFalse();
        assertThat(sse.metadata()).isEqualTo("meta");
        assertThat(sse.createdAt()).isEqualTo(now);
    }

    @Test
    void sseNotificationEntity_recordEquality() {
        // Given two equal SSE entities / Then record equality and toString hold
        SseNotificationEntity a = new SseNotificationEntity(1L, 2L, "T", "t", "m", "IN_APP", false, "meta", null);
        SseNotificationEntity b = new SseNotificationEntity(1L, 2L, "T", "t", "m", "IN_APP", false, "meta", null);
        SseNotificationEntity c = new SseNotificationEntity(9L, 2L, "T", "t", "m", "IN_APP", false, "meta", null);
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b).isNotEqualTo(c);
        assertThat(a.toString()).contains("SseNotificationEntity");
    }
}
