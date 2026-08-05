package com.financialapp.notifications.application.usecase.event.impl;

import com.financialapp.notifications.application.service.NotificationChannelResolver;
import com.financialapp.notifications.domain.event.ImportStale;
import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import com.financialapp.notifications.domain.service.NotificationService;
import com.financialapp.notifications.domain.usecase.event.ProcessImportStaleUseCase;
import com.financialapp.notifications.domain.usecase.event.command.ProcessImportStaleCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ProcessImportStaleUseCaseImpl implements ProcessImportStaleUseCase {

    private static final Locale MESSAGE_LOCALE = Locale.of("es", "AR");

    private final NotificationService notificationService;
    private final NotificationChannelResolver channelResolver;

    @Override
    public void execute(ProcessImportStaleCommand command) {
        ImportStale s = command.importStale();

        String title = "Import Health Warning: Stale Account";
        String message = String.format(MESSAGE_LOCALE,
                "No bank imports have been recorded for account %s in %d days.",
                s.accountCbu(), s.daysSinceImport());

        channelResolver.resolve(s.userId(), NotificationType.IMPORT_STALE).ifPresent(channel -> {
            Notification newNotification = Notification.create(
                    s.userId(), NotificationType.IMPORT_STALE, title, message, channel, null);
            notificationService.notify(newNotification);
        });
    }
}
