package com.financialapp.notifications.kafka.listener;

import com.financialapp.notifications.kafka.event.UserRegisteredEvent;
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
public class UserEventListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "user.registered", groupId = "notifications-group")
    public void handleUserRegistered(UserRegisteredEvent event) {
        log.info("Received user.registered event for userId={}", event.getUserId());
        UserRegisteredEvent.Payload p = event.getPayload();

        // Create notification preferences row for this user
        notificationService.createPreferenceIfAbsent(event.getUserId(), p.getEmail());

        String title = "Welcome to Financial App!";
        String message = String.format(
                "Hi %s, your account has been created successfully. "
                        + "Start tracking your finances, investments, and more.",
                p.getFirstName());

        notificationService.createAndDispatch(
                event.getUserId(), NotificationType.USER_REGISTERED, title, message,
                NotificationChannel.BOTH, null);
    }
}
