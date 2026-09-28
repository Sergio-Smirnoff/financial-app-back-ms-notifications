package com.financialapp.notifications.application.usecase.notification.impl;

import com.financialapp.notifications.application.service.MonthlySummaryDelivery;
import com.financialapp.notifications.domain.repository.UserNotificationPreferenceRepository;
import com.financialapp.notifications.domain.usecase.notification.EnsureMonthlySummaryUseCase;
import com.financialapp.notifications.domain.usecase.notification.MonthlySummaryResult;
import com.financialapp.notifications.domain.usecase.notification.MonthlySummarySkip;
import com.financialapp.notifications.domain.usecase.notification.command.EnsureMonthlySummaryCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class EnsureMonthlySummaryUseCaseImpl implements EnsureMonthlySummaryUseCase {

    private final UserNotificationPreferenceRepository recipients;
    private final MonthlySummaryDelivery delivery;
    private final Clock clock;

    @Override
    public MonthlySummaryResult execute(EnsureMonthlySummaryCommand command) {
        YearMonth month = YearMonth.now(clock).minusMonths(1);
        return recipients.findByUserId(command.userId())
                .map(recipient -> delivery.deliver(recipient, month))
                .orElseGet(() -> new MonthlySummaryResult(false, month, MonthlySummarySkip.NO_RECIPIENT));
    }
}
