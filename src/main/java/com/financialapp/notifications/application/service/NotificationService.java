package com.financialapp.notifications.application.service;

import com.financialapp.notifications.domain.messaging.EmailSender;
import com.financialapp.notifications.domain.messaging.InAppNotificationSender;
import com.financialapp.notifications.domain.repository.NotificationRepository;
import com.financialapp.notifications.domain.repository.UserNotificationPreferenceRepository;
import com.financialapp.notifications.domain.model.entity.Notification;
import com.financialapp.notifications.domain.model.entity.UserNotificationPreference;
import com.financialapp.notifications.domain.model.entity.enums.NotificationChannel;
import com.financialapp.notifications.domain.model.entity.enums.NotificationType;
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

    @Transactional
    public void notify(Long userId, NotificationType type,
                                  String title, String message,
                                  NotificationChannel channel, String metadata) {
        Notification notification = Notification.builder()
                .userId(userId)
                .type(type)
                .title(title)
                .message(message)
                .channel(channel)
                .read(false)
                .metadata(metadata)
                .build();

        Notification saved = notificationRepository.save(notification);
        NotificationResponse response = notificationMapper.toResponse(saved);

        // Push via SSE for in-app channels
        if (channel == NotificationChannel.IN_APP || channel == NotificationChannel.BOTH) {
            inAppNotificationSender.sendToUser(userId, response);
        }

        // Send email for email channels
        if (channel == NotificationChannel.EMAIL || channel == NotificationChannel.BOTH) {
            preferenceRepository.findByUserId(userId)
                    .ifPresent(pref -> emailSender.sendSimpleNotification(pref.getEmail(), title, message));
        }
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
