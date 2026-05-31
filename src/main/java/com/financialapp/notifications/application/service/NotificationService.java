package com.financialapp.notifications.application.service;

import com.financialapp.notifications.domain.messaging.EmailSender;
import com.financialapp.notifications.domain.messaging.InAppNotificationSender;
import com.financialapp.notifications.domain.repository.NotificationRepository;
import com.financialapp.notifications.domain.repository.UserNotificationPreferenceRepository;
import com.financialapp.notifications.domain.model.entity.Notification;
import com.financialapp.notifications.domain.model.entity.UserNotificationPreference;
import com.financialapp.notifications.domain.model.entity.enums.NotificationChannel;
import com.financialapp.notifications.domain.model.response.NotificationResponse;
import com.financialapp.notifications.web.controller.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserNotificationPreferenceRepository preferenceRepository;
    private final NotificationMapper notificationMapper;
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
        NotificationResponse response = notificationMapper.toResponse(notification);
        inAppNotificationSender.sendToUser(notification.userId(), response);
    }

    private void dispatchEmail(Notification notification) {
        preferenceRepository.findByUserId(notification.userId())
                .ifPresent(pref -> emailSender.sendSimpleNotification(pref.getEmail(), notification.title(), notification.message()));
    }

    @Transactional
    public void createPreferenceIfAbsent(Long userId, String email) {
        if (preferenceRepository.findByUserId(userId).isEmpty()) {
            preferenceRepository.save(UserNotificationPreference.builder()
                    .userId(userId)
                    .email(email)
                    .monthlyEmailEnabled(true)
                    .build());
            log.info("Created notification preferences for userId={}", userId);
        }
    }
}
