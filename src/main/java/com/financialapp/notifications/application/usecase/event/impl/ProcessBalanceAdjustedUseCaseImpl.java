package com.financialapp.notifications.application.usecase.event.impl;

import com.financialapp.notifications.application.service.NotificationChannelResolver;
import com.financialapp.notifications.domain.event.BalanceAdjusted;
import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import com.financialapp.notifications.domain.service.NotificationService;
import com.financialapp.notifications.domain.usecase.event.ProcessBalanceAdjustedUseCase;
import com.financialapp.notifications.domain.usecase.event.command.ProcessBalanceAdjustedCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ProcessBalanceAdjustedUseCaseImpl implements ProcessBalanceAdjustedUseCase {

    private static final Locale MESSAGE_LOCALE = Locale.of("es", "AR");

    private final NotificationService notificationService;
    private final NotificationChannelResolver channelResolver;

    @Override
    public void execute(ProcessBalanceAdjustedCommand command) {
        BalanceAdjusted ba = command.balanceAdjusted();
        String direction = ba.credit() ? "credited" : "debited";
        String title = String.format("Balance %s: %s", ba.credit() ? "Credited" : "Debited", ba.accountName());
        String message = String.format(MESSAGE_LOCALE,
                "Your account '%s' was %s %.2f %s.",
                ba.accountName(), direction, ba.amount().doubleValue(), ba.currency());

        NotificationChannel channel = channelResolver.resolve(ba.userId());
        var newNotification = Notification.create(
                ba.userId(), NotificationType.BALANCE_ADJUSTED, title, message, channel, null);
        notificationService.notify(newNotification);
    }
}
