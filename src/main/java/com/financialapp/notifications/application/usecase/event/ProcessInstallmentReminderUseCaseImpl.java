package com.financialapp.notifications.application.usecase.event;

import com.financialapp.notifications.application.service.NotificationService;
import com.financialapp.notifications.domain.interfaces.usecase.event.ProcessInstallmentReminderUseCase;
import com.financialapp.notifications.domain.model.entity.Notification;
import com.financialapp.notifications.domain.model.entity.enums.NotificationChannel;
import com.financialapp.notifications.domain.model.entity.enums.NotificationType;
import com.financialapp.notifications.domain.model.entity.event.InstallmentReminder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessInstallmentReminderUseCaseImpl implements ProcessInstallmentReminderUseCase {
    private final NotificationService notificationService;

    @Override
    public void execute(InstallmentReminder reminder) {
        String title = String.format("Installment #%d Due: %s", reminder.installmentNumber(), reminder.loanDescription());
        String message = String.format(
                "Installment #%d of %.2f %s for loan '%s' is due on %s.",
                reminder.installmentNumber(), reminder.amount().doubleValue(), reminder.currency(),
                reminder.loanDescription(), reminder.dueDate());
        var newNotification = Notification.create(
                reminder.userId(), NotificationType.INSTALLMENT_REMINDER, title, message,
                NotificationChannel.BOTH, null);
        notificationService.notify(newNotification);
    }
}
