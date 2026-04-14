package com.financialapp.notifications.kafka.listener;

import com.financialapp.notifications.kafka.event.InstallmentReminderEvent;
import com.financialapp.notifications.kafka.event.LoanReminderEvent;
import com.financialapp.notifications.kafka.event.PaymentDueEvent;
import com.financialapp.notifications.model.enums.NotificationChannel;
import com.financialapp.notifications.model.enums.NotificationType;
import com.financialapp.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FinancesEventListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "payment.due", groupId = "notifications-group")
    public void handlePaymentDue(PaymentDueEvent event) {
        log.info("Received payment.due event for userId={}", event.getUserId());
        PaymentDueEvent.Payload p = event.getPayload();
        String title = "Payment Due: " + p.getDescription();
        String message = String.format(
                "Your payment of %s %s for '%s' is due on %s. %d installment(s) remaining.",
                p.getInstallmentAmount(), p.getCurrency(), p.getDescription(),
                p.getNextDueDate(), p.getRemainingInstallments());
        notificationService.createAndDispatch(
                event.getUserId(), NotificationType.PAYMENT_DUE, title, message,
                NotificationChannel.BOTH, null);
    }

    @KafkaListener(topics = "loan.reminder", groupId = "notifications-group")
    public void handleLoanReminder(LoanReminderEvent event) {
        log.info("Received loan.reminder event for userId={}", event.getUserId());
        LoanReminderEvent.Payload p = event.getPayload();
        String title = "Loan Payment Due: " + p.getLoanDescription();
        String message = String.format(
                "Your loan payment of %s %s for '%s' is due on %s. %d installment(s) remaining.",
                p.getInstallmentAmount(), p.getCurrency(), p.getLoanDescription(),
                p.getNextPaymentDate(), p.getRemainingInstallments());
        notificationService.createAndDispatch(
                event.getUserId(), NotificationType.LOAN_REMINDER, title, message,
                NotificationChannel.BOTH, null);
    }

    @KafkaListener(topics = "installment.reminder", groupId = "notifications-group")
    public void handleInstallmentReminder(InstallmentReminderEvent event) {
        log.info("Received installment.reminder event for userId={}", event.getUserId());
        InstallmentReminderEvent.Payload p = event.getPayload();
        String title = String.format("Installment #%d Due: %s", p.getInstallmentNumber(), p.getLoanDescription());
        String message = String.format(
                "Installment #%d of %s %s for loan '%s' is due on %s.",
                p.getInstallmentNumber(), p.getAmount(), p.getCurrency(),
                p.getLoanDescription(), p.getDueDate());
        notificationService.createAndDispatch(
                event.getUserId(), NotificationType.INSTALLMENT_REMINDER, title, message,
                NotificationChannel.BOTH, null);
    }
}
