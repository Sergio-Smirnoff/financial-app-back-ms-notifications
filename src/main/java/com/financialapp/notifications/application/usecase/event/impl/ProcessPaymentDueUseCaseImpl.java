package com.financialapp.notifications.application.usecase.event.impl;

import com.financialapp.notifications.application.service.NotificationChannelResolver;
import com.financialapp.notifications.domain.service.NotificationService;
import com.financialapp.notifications.domain.usecase.event.ProcessPaymentDueUseCase;
import com.financialapp.notifications.domain.usecase.event.command.ProcessPaymentDueCommand;
import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.event.PaymentDue;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ProcessPaymentDueUseCaseImpl implements ProcessPaymentDueUseCase {

    private static final Locale MESSAGE_LOCALE = Locale.of("es", "AR");

    private final NotificationService notificationService;
    private final NotificationChannelResolver channelResolver;

    @Override
    public void execute(ProcessPaymentDueCommand command) {
        PaymentDue paymentDue = command.paymentDue();
        String title = "Payment Due: " + paymentDue.description();
        String message = String.format(MESSAGE_LOCALE,
                "Installment %d/%d of %.2f %s for '%s' is due on %s.",
                paymentDue.installmentNumber(), paymentDue.totalInstallments(),
                paymentDue.amount().doubleValue(), paymentDue.currency(),
                paymentDue.description(), paymentDue.dueDate());

        NotificationChannel channel = channelResolver.resolve(paymentDue.userId());
        var newNotification = Notification.create(
                paymentDue.userId(), NotificationType.PAYMENT_DUE, title, message, channel, null);
        notificationService.notify(newNotification);
    }
}
