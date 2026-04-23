package com.financialapp.notifications.service;

import com.financialapp.notifications.exception.ResourceNotFoundException;
import com.financialapp.notifications.mapper.NotificationMapper;
import com.financialapp.notifications.model.dto.response.NotificationPreferenceResponse;
import com.financialapp.notifications.model.dto.response.NotificationResponse;
import com.financialapp.notifications.model.dto.response.UnreadCountResponse;
import com.financialapp.notifications.model.entity.Notification;
import com.financialapp.notifications.model.entity.UserNotificationPreference;
import com.financialapp.notifications.model.enums.NotificationChannel;
import com.financialapp.notifications.model.enums.NotificationType;
import com.financialapp.notifications.repository.NotificationRepository;
import com.financialapp.notifications.repository.UserNotificationPreferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserNotificationPreferenceRepository preferenceRepository;
    private final NotificationMapper notificationMapper;
    private final SseEmitterService sseEmitterService;
    private final EmailService emailService;

    @Transactional
    public NotificationResponse createAndDispatch(Long userId, NotificationType type,
                                                  String title, String message,
                                                  NotificationChannel channel, String metadata) {
        Notification notification = Notification.builder()
                .userId(userId)
                .type(type)
                .title(title)
                .message(message)
                .channel(channel)
                .metadata(metadata)
                .build();

        Notification saved = notificationRepository.save(notification);
        NotificationResponse response = notificationMapper.toResponse(saved);

        // Push via SSE for in-app channels
        if (channel == NotificationChannel.IN_APP || channel == NotificationChannel.BOTH) {
            sseEmitterService.sendToUser(userId, response);
        }

        // Send email for email channels
        if (channel == NotificationChannel.EMAIL || channel == NotificationChannel.BOTH) {
            preferenceRepository.findByUserId(userId).ifPresent(pref ->
                    emailService.sendSimpleNotification(pref.getEmail(), title, message)
            );
        }

        return response;
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(notificationMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getLatest(Long userId) {
        return notificationRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getLatestByBank(Long userId, Long bankId) {
        return notificationRepository.findLatestByBank(userId, bankId.toString())
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UnreadCountResponse getUnreadCount(Long userId) {
        return UnreadCountResponse.builder()
                .count(notificationRepository.countByUserIdAndReadFalse(userId))
                .build();
    }

    @Transactional
    public void markAsRead(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .filter(n -> n.getUserId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Notification", notificationId));
        notification.setRead(true);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId);
    }

    // ── Preferences ──────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public NotificationPreferenceResponse getPreference(Long userId) {
        return preferenceRepository.findByUserId(userId)
                .map(this::toPreferenceResponse)
                .orElse(NotificationPreferenceResponse.builder()
                        .userId(userId)
                        .email("")
                        .monthlyEmailEnabled(true)
                        .build());
    }

    @Transactional
    public NotificationPreferenceResponse updatePreference(Long userId, boolean monthlyEmailEnabled) {
        UserNotificationPreference pref = preferenceRepository.findByUserId(userId)
                .orElseGet(() -> UserNotificationPreference.builder()
                        .userId(userId)
                        .email("")
                        .monthlyEmailEnabled(true)
                        .build());
        pref.setMonthlyEmailEnabled(monthlyEmailEnabled);
        return toPreferenceResponse(preferenceRepository.save(pref));
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

    private NotificationPreferenceResponse toPreferenceResponse(UserNotificationPreference pref) {
        return NotificationPreferenceResponse.builder()
                .userId(pref.getUserId())
                .email(pref.getEmail())
                .monthlyEmailEnabled(pref.isMonthlyEmailEnabled())
                .build();
    }
}
