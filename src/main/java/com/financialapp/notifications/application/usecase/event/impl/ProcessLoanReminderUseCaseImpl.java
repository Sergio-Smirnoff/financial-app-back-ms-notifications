package com.financialapp.notifications.application.usecase.event.impl;

import com.financialapp.notifications.domain.service.NotificationService;
import com.financialapp.notifications.domain.usecase.event.ProcessLoanReminderUseCase;
import com.financialapp.notifications.domain.usecase.event.command.ProcessLoanReminderCommand;
import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.event.LoanReminder;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessLoanReminderUseCaseImpl implements ProcessLoanReminderUseCase {

    private final NotificationService notificationService;

    @Override
    public void execute(ProcessLoanReminderCommand command) {
        LoanReminder reminder = command.reminder();
        String title = "Loan Payment Due: " + reminder.loanDescription();
        String message = String.format(
                "Your loan payment of %.2f %s for '%s' is due on %s. %d installment(s) remaining.",
                reminder.installmentAmount().doubleValue(), reminder.currency(), reminder.loanDescription(),
                reminder.nextPaymentDate(), reminder.remainingInstallments());

        var newNotification = Notification.create(
                reminder.userId(), NotificationType.LOAN_REMINDER, title, message,
                NotificationChannel.BOTH, null);
        notificationService.notify(newNotification);
    }
}
