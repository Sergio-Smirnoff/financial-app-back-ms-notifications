package com.financialapp.notifications.application.usecase.preference;

import com.financialapp.notifications.domain.repository.UserNotificationPreferenceRepository;
import com.financialapp.notifications.domain.model.entity.UserNotificationPreference;
import com.financialapp.notifications.domain.usecase.UpdatePreferenceUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdatePreferenceUseCaseImpl implements UpdatePreferenceUseCase {

    private final UserNotificationPreferenceRepository preferenceRepository;

    @Transactional
    public UserNotificationPreference execute(Long userId, boolean monthlyEmailEnabled) {
        UserNotificationPreference preference = preferenceRepository.findByUserId(userId)
                .orElseGet(() -> UserNotificationPreference.builder()
                        .userId(userId)
                        .email("")
                        .monthlyEmailEnabled(true)
                        .build());

        preference = preference.withMonthlyEmailEnabled(monthlyEmailEnabled);
        return preferenceRepository.save(preference);
    }
}