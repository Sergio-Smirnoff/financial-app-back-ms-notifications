package com.financialapp.notifications.kafka.listener;

import com.financialapp.notifications.kafka.event.BankAlertEvent;
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
public class BankEventListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "bank-alerts", groupId = "notifications-group")
    public void handleBankAlert(BankAlertEvent event) {
        log.info("Received bank-alert event of type {} for userId={}", event.getType(), event.getUserId());
        
        NotificationType type;
        try {
            type = NotificationType.valueOf(event.getType());
        } catch (IllegalArgumentException e) {
            log.warn("Unknown bank alert type: {}", event.getType());
            return;
        }

        notificationService.createAndDispatch(
                event.getUserId(), type, event.getTitle(), event.getMessage(),
                NotificationChannel.BOTH, event.getMetadata());
    }
}
