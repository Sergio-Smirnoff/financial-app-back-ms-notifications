package com.financialapp.notifications.application.service;

import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.repository.UserNotificationPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationChannelResolver {

    private final UserNotificationPreferenceRepository preferenceRepository;

    public NotificationChannel resolve(Long userId) {
        return preferenceRepository.findByUserId(userId)
                .map(preference -> preference.monthlyEmailEnabled()
                        ? NotificationChannel.BOTH
                        : NotificationChannel.IN_APP)
                .orElse(NotificationChannel.IN_APP);
    }
}
