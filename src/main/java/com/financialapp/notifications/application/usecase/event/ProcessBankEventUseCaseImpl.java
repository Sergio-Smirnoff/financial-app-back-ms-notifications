package com.financialapp.notifications.application.usecase.event;

import com.financialapp.notifications.application.service.NotificationService;
import com.financialapp.notifications.domain.model.entity.Notification;
import com.financialapp.notifications.domain.model.entity.enums.NotificationChannel;
import com.financialapp.notifications.domain.model.entity.enums.NotificationType;
import com.financialapp.notifications.domain.model.entity.event.BankAlert;
import com.financialapp.notifications.domain.usecase.event.ProcessBankEventUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessBankEventUseCaseImpl implements ProcessBankEventUseCase {

    private final NotificationService notificationService;

    @Override
    public void execute(BankAlert alert) {
        NotificationType type;
        try {
            type = NotificationType.valueOf(alert.type());
        } catch (IllegalArgumentException e) {
            log.warn("Unknown bank alert type: {}", alert.type());
            return;
        }

        var newNotification = Notification.create(alert.userId(), type, alert.title(), alert.message(),
                NotificationChannel.BOTH, alert.metadata());

        notificationService.notify(
                newNotification);
    }
}
