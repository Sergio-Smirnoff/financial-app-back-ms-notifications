package com.financialapp.notifications.domain.model.response;

import com.financialapp.notifications.web.controller.dto.NotificationPreferenceResponse;
import com.financialapp.notifications.web.controller.dto.NotificationResponse;
import com.financialapp.notifications.web.controller.dto.UnreadCountResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseDtosTest {

    @Test
    void notificationPreferenceResponse_buildsAndExposesFields() {
        // Given / When built
        NotificationPreferenceResponse r = NotificationPreferenceResponse.builder()
                .userId(1L).email("e@x.com").monthlyEmailEnabled(true).build();

        // Then accessors expose the fields
        assertThat(r.getUserId()).isEqualTo(1L);
        assertThat(r.getEmail()).isEqualTo("e@x.com");
        assertThat(r.isMonthlyEmailEnabled()).isTrue();
    }

    @Test
    void notificationResponse_buildsAndExposesFields() {
        // Given / When built
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        NotificationResponse r = NotificationResponse.builder()
                .id(1L).userId(2L).type("PAYMENT_DUE").title("t").message("m").channel("BOTH")
                .read(true).metadata("meta").createdAt(now).build();

        // Then accessors expose the fields
        assertThat(r.getId()).isEqualTo(1L);
        assertThat(r.getUserId()).isEqualTo(2L);
        assertThat(r.getType()).isEqualTo("PAYMENT_DUE");
        assertThat(r.getTitle()).isEqualTo("t");
        assertThat(r.getMessage()).isEqualTo("m");
        assertThat(r.getChannel()).isEqualTo("BOTH");
        assertThat(r.isRead()).isTrue();
        assertThat(r.getMetadata()).isEqualTo("meta");
        assertThat(r.getCreatedAt()).isEqualTo(now);
    }

    @Test
    void unreadCountResponse_buildsAndExposesCount() {
        // Given / When built
        UnreadCountResponse r = UnreadCountResponse.builder().count(7L).build();

        // Then the count is exposed
        assertThat(r.getCount()).isEqualTo(7L);
    }
}
