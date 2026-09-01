package com.financialapp.notifications.application.usecase.event.impl;

import com.financialapp.notifications.application.service.NotificationChannelResolver;
import com.financialapp.notifications.domain.event.BudgetThresholdReached;
import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import com.financialapp.notifications.domain.service.NotificationService;
import com.financialapp.notifications.domain.usecase.event.ProcessBudgetThresholdUseCase;
import com.financialapp.notifications.domain.usecase.event.command.ProcessBudgetThresholdCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ProcessBudgetThresholdUseCaseImpl implements ProcessBudgetThresholdUseCase {

    private static final Locale MESSAGE_LOCALE = Locale.of("es", "AR");

    private final NotificationService notificationService;
    private final NotificationChannelResolver channelResolver;

    @Override
    public void execute(ProcessBudgetThresholdCommand command) {
        BudgetThresholdReached b = command.budgetThreshold();

        String title = String.format(MESSAGE_LOCALE, "Budget Alert: Threshold Reached (%.1f%%)", b.pctUsed());
        String message = String.format(MESSAGE_LOCALE,
                "Your budget spend has reached %.1f%% of your configured limit (threshold: %.1f%%).",
                b.pctUsed(), b.alertThresholdPct());

        channelResolver.resolve(b.userId(), NotificationType.BUDGET_THRESHOLD_REACHED).ifPresent(channel -> {
            Notification newNotification = Notification.create(
                    b.userId(), NotificationType.BUDGET_THRESHOLD_REACHED, title, message, channel, null);
            notificationService.notify(newNotification);
        });
    }
}
