package com.financialapp.notifications.application.usecase.event.impl;

import com.financialapp.notifications.domain.service.NotificationService;
import com.financialapp.notifications.domain.usecase.event.ProcessInstallmentReminderUseCase;
import com.financialapp.notifications.domain.usecase.event.command.ProcessInstallmentReminderCommand;
import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import com.financialapp.notifications.domain.event.InstallmentReminder;
import com.financialapp.notifications.domain.usecase.preference.GetPreferenceUseCase;
import com.financialapp.notifications.domain.usecase.preference.command.GetPreferenceCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ProcessInstallmentReminderUseCaseImpl implements ProcessInstallmentReminderUseCase {

    private static final Locale MESSAGE_LOCALE = Locale.of("es", "AR");

    private final NotificationService notificationService;
    private final GetPreferenceUseCase getPreferenceUseCase;

    @Override
    public void execute(ProcessInstallmentReminderCommand command) {
        InstallmentReminder reminder = command.reminder();
        String title = String.format("Installment #%d Due: %s", reminder.installmentNumber(), reminder.loanDescription());
        String message = String.format(MESSAGE_LOCALE,
                "Installment #%d of %.2f %s for loan '%s' is due on %s.",
                reminder.installmentNumber(), reminder.amount().doubleValue(), reminder.currency(),
                reminder.loanDescription(), reminder.dueDate());

        NotificationChannel channel = resolveChannel(reminder.userId());
        var newNotification = Notification.create(
                reminder.userId(), NotificationType.INSTALLMENT_REMINDER, title, message, channel, null);
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
