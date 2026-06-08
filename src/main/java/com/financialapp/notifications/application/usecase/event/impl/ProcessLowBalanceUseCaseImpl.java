package com.financialapp.notifications.application.usecase.event.impl;

import com.financialapp.notifications.domain.event.LowBalance;
import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import com.financialapp.notifications.domain.service.NotificationService;
import com.financialapp.notifications.domain.usecase.event.ProcessLowBalanceUseCase;
import com.financialapp.notifications.domain.usecase.event.command.ProcessLowBalanceCommand;
import com.financialapp.notifications.domain.usecase.preference.GetPreferenceUseCase;
import com.financialapp.notifications.domain.usecase.preference.command.GetPreferenceCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ProcessLowBalanceUseCaseImpl implements ProcessLowBalanceUseCase {

    private static final Locale MESSAGE_LOCALE = Locale.of("es", "AR");

    private final NotificationService notificationService;
    private final GetPreferenceUseCase getPreferenceUseCase;

    @Override
    public void execute(ProcessLowBalanceCommand command) {
        LowBalance lb = command.lowBalance();
        String title = "Low Balance Alert: " + lb.accountName();
        String message = String.format(MESSAGE_LOCALE,
                "Your account '%s' has a low balance of %.2f %s. Please review your finances.",
                lb.accountName(), lb.balance().doubleValue(), lb.currency());

        NotificationChannel channel = resolveChannel(lb.userId());
        var newNotification = Notification.create(
                lb.userId(), NotificationType.LOW_BALANCE, title, message, channel, null);
        notificationService.notify(newNotification);
    }

    private NotificationChannel resolveChannel(Long userId) {
        try {
            getPreferenceUseCase.execute(new GetPreferenceCommand(userId));
            return NotificationChannel.BOTH;
        } catch (Exception e) {
            return NotificationChannel.IN_APP;
        }
    }
}
