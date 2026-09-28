package com.financialapp.notifications.application.usecase.notification;

import com.financialapp.notifications.application.service.MonthlySummaryDelivery;
import com.financialapp.notifications.application.usecase.notification.impl.EnsureMonthlySummaryUseCaseImpl;
import com.financialapp.notifications.domain.model.notification.UserNotificationPreference;
import com.financialapp.notifications.domain.repository.UserNotificationPreferenceRepository;
import com.financialapp.notifications.domain.usecase.notification.MonthlySummaryResult;
import com.financialapp.notifications.domain.usecase.notification.MonthlySummarySkip;
import com.financialapp.notifications.domain.usecase.notification.command.EnsureMonthlySummaryCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnsureMonthlySummaryUseCaseImplTest {

    private static final Clock LAST_EVENING_OF_SEPTEMBER_ART =
            Clock.fixed(Instant.parse("2026-10-01T02:00:00Z"), ZoneId.of("America/Argentina/Buenos_Aires"));
    private static final YearMonth AUGUST = YearMonth.of(2026, 8);

    @Mock private UserNotificationPreferenceRepository recipients;
    @Mock private MonthlySummaryDelivery delivery;
    private EnsureMonthlySummaryUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new EnsureMonthlySummaryUseCaseImpl(recipients, delivery, LAST_EVENING_OF_SEPTEMBER_ART);
    }

    @Test
    void execute_knownRecipient_deliversThePreviousArtMonth_andAnswersWhatTheDeliveryDid() {
        UserNotificationPreference recipient = new UserNotificationPreference(1L, 7L, "u7@example.com", true, null, null);
        MonthlySummaryResult delivered = new MonthlySummaryResult(false, AUGUST, MonthlySummarySkip.ALREADY_SENT);
        when(recipients.findByUserId(7L)).thenReturn(Optional.of(recipient));
        when(delivery.deliver(recipient, AUGUST)).thenReturn(delivered);

        assertThat(useCase.execute(new EnsureMonthlySummaryCommand(7L))).isEqualTo(delivered);
    }

    @Test
    void execute_noAddressOnFile_isNoRecipient() {
        when(recipients.findByUserId(7L)).thenReturn(Optional.empty());

        assertThat(useCase.execute(new EnsureMonthlySummaryCommand(7L)))
                .isEqualTo(new MonthlySummaryResult(false, AUGUST, MonthlySummarySkip.NO_RECIPIENT));
        verifyNoInteractions(delivery);
    }

    @Test
    void skipCodes_areTheWireValues() {
        assertThat(MonthlySummarySkip.ALREADY_SENT.code()).isEqualTo("already-sent");
        assertThat(MonthlySummarySkip.OPTED_OUT.code()).isEqualTo("opted-out");
        assertThat(MonthlySummarySkip.NO_RECIPIENT.code()).isEqualTo("no-recipient");
    }
}
