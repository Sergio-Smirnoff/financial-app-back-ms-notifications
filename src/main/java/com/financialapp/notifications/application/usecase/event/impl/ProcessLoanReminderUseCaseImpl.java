package com.financialapp.notifications.application.usecase.event.impl;

import com.financialapp.notifications.application.service.NotificationChannelResolver;
import com.financialapp.notifications.domain.service.NotificationService;
import com.financialapp.notifications.domain.usecase.event.ProcessLoanReminderUseCase;
import com.financialapp.notifications.domain.usecase.event.command.ProcessLoanReminderCommand;
import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.event.LoanReminder;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ProcessLoanReminderUseCaseImpl implements ProcessLoanReminderUseCase {

    private static final Locale MESSAGE_LOCALE = Locale.of("es", "AR");

    private final NotificationService notificationService;
    private final NotificationChannelResolver channelResolver;

    @Override
    public void execute(ProcessLoanReminderCommand command) {
        LoanReminder reminder = command.reminder();
        String title = "Loan Payment Due: " + reminder.loanName();
        String message = String.format(MESSAGE_LOCALE,
                "Installment #%d of loan '%s' is due on %s.",
                reminder.installmentNumber(), reminder.loanName(), reminder.dueDate());

        channelResolver.resolve(reminder.userId(), NotificationType.LOAN_REMINDER).ifPresent(channel -> {
            var newNotification = Notification.create(
                    reminder.userId(), NotificationType.LOAN_REMINDER, title, message, channel, null);
            notificationService.notify(newNotification);
        });
    }
}
