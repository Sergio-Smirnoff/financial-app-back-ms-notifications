package com.financialapp.notifications.application.service;

import com.financialapp.notifications.domain.model.notification.NotificationCategory;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationPreference;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import com.financialapp.notifications.domain.repository.NotificationPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class NotificationChannelResolver {

    private final NotificationPreferenceRepository preferenceRepository;

    public Optional<NotificationChannel> resolve(Long userId, NotificationType type) {
        NotificationCategory category = NotificationCategory.forType(type);
        NotificationPreference preference = preferenceRepository.findByUserIdAndCategory(userId, category)
                .orElseGet(() -> preferenceRepository.save(NotificationPreference.defaults(userId, category)));

        if (preference.inAppEnabled() && preference.emailEnabled()) {
            return Optional.of(NotificationChannel.BOTH);
        } else if (preference.inAppEnabled()) {
            return Optional.of(NotificationChannel.IN_APP);
        } else if (preference.emailEnabled()) {
            return Optional.of(NotificationChannel.EMAIL);
        } else {
            return Optional.empty();
        }
    }
}
