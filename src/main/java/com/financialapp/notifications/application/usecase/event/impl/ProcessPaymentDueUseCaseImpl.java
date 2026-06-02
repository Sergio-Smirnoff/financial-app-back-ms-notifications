package com.financialapp.notifications.application.usecase.event.impl;

import com.financialapp.notifications.application.service.NotificationService;
import com.financialapp.notifications.domain.usecase.event.ProcessPaymentDueUseCase;
import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.event.PaymentDue;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessPaymentDueUseCaseImpl implements ProcessPaymentDueUseCase {

    private final NotificationService notificationService;

    @Override
    public void execute(PaymentDue paymentDue) {
        String title = "Payment Due: " + paymentDue.description();
        String message = String.format(
                "Your payment of %.2f %s for '%s' is due on %s. %d installment(s) remaining.",
                paymentDue.installmentAmount().doubleValue(), paymentDue.currency(), paymentDue.description(),
                paymentDue.nextDueDate(), paymentDue.remainingInstallments());

        var newNotification = Notification.create(
                paymentDue.userId(), NotificationType.PAYMENT_DUE, title, message,
                NotificationChannel.BOTH, null);
        notificationService.notify(newNotification);
    }
}
