package com.financialapp.notifications.application.usecase.event.impl;

import com.financialapp.notifications.domain.service.NotificationService;
import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import com.financialapp.notifications.domain.event.BankAlert;
import com.financialapp.notifications.domain.usecase.event.ProcessBankEventUseCase;
import com.financialapp.notifications.domain.usecase.event.command.ProcessBankEventCommand;
import com.financialapp.notifications.domain.usecase.preference.GetPreferenceUseCase;
import com.financialapp.notifications.domain.usecase.preference.command.GetPreferenceCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessBankEventUseCaseImpl implements ProcessBankEventUseCase {

    private final NotificationService notificationService;
    private final GetPreferenceUseCase getPreferenceUseCase;

    @Override
    public void execute(ProcessBankEventCommand command) {
        BankAlert alert = command.alert();
        NotificationType type;
        try {
            type = NotificationType.valueOf(alert.type());
        } catch (IllegalArgumentException e) {
            log.warn("Unknown bank alert type: {}", alert.type());
            return;
        }

        NotificationChannel channel = resolveChannel(alert.userId());
        var newNotification = Notification.create(alert.userId(), type, alert.title(), alert.message(),
                channel, alert.metadata());

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
