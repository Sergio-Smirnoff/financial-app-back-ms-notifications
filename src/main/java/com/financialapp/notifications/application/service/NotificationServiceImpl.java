package com.financialapp.notifications.application.service;

import com.financialapp.notifications.domain.messaging.EmailSender;
import com.financialapp.notifications.domain.messaging.InAppNotificationSender;
import com.financialapp.notifications.domain.model.notification.DeliveryStatus;
import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationDelivery;
import com.financialapp.notifications.domain.repository.NotificationDeliveryRepository;
import com.financialapp.notifications.domain.repository.NotificationRepository;
import com.financialapp.notifications.domain.repository.UserNotificationPreferenceRepository;
import com.financialapp.notifications.domain.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private static final long RETRY_BACKOFF_MINUTES = 5L;

    private final NotificationRepository notificationRepository;
    private final UserNotificationPreferenceRepository preferenceRepository;
    private final InAppNotificationSender inAppNotificationSender;
    private final EmailSender emailSender;
    private final NotificationDeliveryRepository deliveryRepository;

    public void notify(Notification newNotification) {
        var saved = notificationRepository.save(newNotification);
        dispatch(saved);
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
        NotificationDelivery delivery = deliveryRepository.save(
                NotificationDelivery.pending(notification.id(), NotificationChannel.IN_APP));
        try {
            inAppNotificationSender.sendToUser(notification.userId(), notification);
            deliveryRepository.updateStatus(delivery.markSent());
        } catch (Exception e) {
            log.warn("In-app delivery failed for notification {}: {}", notification.id(), e.getMessage());
            deliveryRepository.updateStatus(delivery.markFailed(
                    e.getMessage(), LocalDateTime.now().plusMinutes(RETRY_BACKOFF_MINUTES)));
        }
    }

    private void dispatchEmail(Notification notification) {
        Optional<String> emailOpt = preferenceRepository.findByUserId(notification.userId())
                .map(pref -> pref.email());
        if (emailOpt.isEmpty()) {
            return;
        }
        NotificationDelivery delivery = deliveryRepository.save(
                NotificationDelivery.pending(notification.id(), NotificationChannel.EMAIL));
        try {
            emailSender.sendSimpleNotification(emailOpt.get(), notification.title(), notification.message());
            deliveryRepository.updateStatus(delivery.markSent());
        } catch (Exception e) {
            log.warn("Email delivery failed for notification {}: {}", notification.id(), e.getMessage());
            deliveryRepository.updateStatus(delivery.markFailed(
                    e.getMessage(), LocalDateTime.now().plusMinutes(RETRY_BACKOFF_MINUTES)));
        }
    }

    public void retryDelivery(NotificationDelivery delivery, Notification notification) {
        try {
            if (delivery.channel() == NotificationChannel.IN_APP) {
                inAppNotificationSender.sendToUser(notification.userId(), notification);
            } else if (delivery.channel() == NotificationChannel.EMAIL) {
                Optional<String> emailOpt = preferenceRepository.findByUserId(notification.userId())
                        .map(pref -> pref.email());
                if (emailOpt.isEmpty()) {
                    return;
                }
                emailSender.sendSimpleNotification(emailOpt.get(), notification.title(), notification.message());
            }
            deliveryRepository.updateStatus(delivery.markSent());
        } catch (Exception e) {
            log.warn("Retry delivery failed for notification {}: {}", notification.id(), e.getMessage());
            deliveryRepository.updateStatus(delivery.markFailed(
                    e.getMessage(), LocalDateTime.now().plusMinutes(RETRY_BACKOFF_MINUTES)));
        }
    }
}
