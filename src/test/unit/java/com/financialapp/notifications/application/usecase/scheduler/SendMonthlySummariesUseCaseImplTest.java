package com.financialapp.notifications.application.usecase.scheduler;

import com.financialapp.notifications.application.service.MonthlySummaryDelivery;
import com.financialapp.notifications.application.usecase.scheduler.impl.SendMonthlySummariesUseCaseImpl;
import com.financialapp.notifications.domain.model.notification.UserNotificationPreference;
import com.financialapp.notifications.domain.model.pagination.PageResult;
import com.financialapp.notifications.domain.repository.UserNotificationPreferenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SendMonthlySummariesUseCaseImplTest {

    private static final Clock LAST_EVENING_OF_SEPTEMBER_ART =
            Clock.fixed(Instant.parse("2026-10-01T02:00:00Z"), ZoneId.of("America/Argentina/Buenos_Aires"));
    private static final YearMonth AUGUST = YearMonth.of(2026, 8);

    @Mock private UserNotificationPreferenceRepository recipients;
    @Mock private MonthlySummaryDelivery delivery;
    private SendMonthlySummariesUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new SendMonthlySummariesUseCaseImpl(recipients, delivery, LAST_EVENING_OF_SEPTEMBER_ART);
    }

    private static UserNotificationPreference recipient(Long userId) {
        return new UserNotificationPreference(userId, userId, "u" + userId + "@example.com", false, null, null);
    }

    @Test
    void execute_pagesOverEveryRecipient_andDeliversThePreviousArtMonth() {
        UserNotificationPreference u1 = recipient(1L);
        UserNotificationPreference u2 = recipient(2L);
        when(recipients.findAll(0, 500)).thenReturn(new PageResult<>(List.of(u1), 0, 500, 1000));
        when(recipients.findAll(1, 500)).thenReturn(new PageResult<>(List.of(u2), 1, 500, 1000));

        useCase.execute();

        verify(delivery).deliver(u1, AUGUST);
        verify(delivery).deliver(u2, AUGUST);
    }

    @Test
    void execute_oneUsersFailure_isSwallowed() {
        UserNotificationPreference u9 = recipient(9L);
        when(recipients.findAll(0, 500)).thenReturn(new PageResult<>(List.of(u9), 0, 500, 1));
        when(delivery.deliver(any(), any())).thenThrow(new RuntimeException("smtp down"));

        useCase.execute();

        verify(delivery).deliver(u9, AUGUST);
    }
}
