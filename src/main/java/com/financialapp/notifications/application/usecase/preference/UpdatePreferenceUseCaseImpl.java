package com.financialapp.notifications.application.usecase.preference;

import com.financialapp.notifications.domain.interfaces.infrastructure.UserNotificationPreferenceRepository;
import com.financialapp.notifications.domain.model.entity.UserNotificationPreference;
import com.financialapp.notifications.domain.model.response.NotificationPreferenceResponse;
import com.financialapp.notifications.domain.usecase.UpdatePreferenceUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdatePreferenceUseCase implements UpdatePreferenceUseCase {

    private final UserNotificationPreferenceRepository preferenceRepository;

    @Transactional
    public NotificationPreferenceResponse execute(Long userId, boolean monthlyEmailEnabled) {
        UserNotificationPreference preference = preferenceRepository.findByUserId(userId)
                .orElseGet(() -> UserNotificationPreference.builder()
                        .userId(userId)
                        .email("")
                        .monthlyEmailEnabled(true)
                        .build());

        preference.setMonthlyEmailEnabled(monthlyEmailEnabled);
        return toResponse(preferenceRepository.save(preference));
    }

    private NotificationPreferenceResponse toResponse(UserNotificationPreference preference) {
        return NotificationPreferenceResponse.builder()
                .userId(preference.getUserId())
                .email(preference.getEmail())
                .monthlyEmailEnabled(preference.isMonthlyEmailEnabled())
                .build();
    }
}