package com.financialapp.notifications.application.service;

import com.financialapp.notifications.domain.messaging.EmailSender;
import com.financialapp.notifications.domain.messaging.InAppNotificationSender;
import com.financialapp.notifications.domain.repository.NotificationRepository;
import com.financialapp.notifications.domain.repository.UserNotificationPreferenceRepository;
import com.financialapp.notifications.domain.model.entity.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserNotificationPreferenceRepository preferenceRepository;
    private final InAppNotificationSender inAppNotificationSender;
    private final EmailSender emailSender;

    public void notify(Notification newNotification) {
        var saved = saveInRepository(newNotification);
        dispatch(saved);
    }

    private Notification saveInRepository(Notification notification) {
        return notificationRepository.save(notification);
    }

    private void dispatch(Notification notification) {
        if (notification.channel().sendInApp()) {
            dispatchInApp(notification);
        }

        if (notification.channel().sendEmail()) {
            dispatchEmail(notification);
        }
    }

    private void dispatchInApp(Notification notification) {
        inAppNotificationSender.sendToUser(notification.userId(), notification);
    }

    private void dispatchEmail(Notification notification) {
        preferenceRepository.findByUserId(notification.userId())
                .ifPresent(pref -> emailSender.sendSimpleNotification(pref.email(), notification.title(),
                        notification.message()));
    }
}
