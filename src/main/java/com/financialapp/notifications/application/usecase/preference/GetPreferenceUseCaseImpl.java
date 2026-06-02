package com.financialapp.notifications.application.usecase.preference;

import com.financialapp.notifications.domain.repository.UserNotificationPreferenceRepository;
import com.financialapp.notifications.domain.model.entity.UserNotificationPreference;
import com.financialapp.notifications.domain.usecase.GetPreferenceUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetPreferenceUseCaseImpl implements GetPreferenceUseCase {

    private final UserNotificationPreferenceRepository preferenceRepository;

    @Transactional(readOnly = true)
    public UserNotificationPreference execute(Long userId) {
        return preferenceRepository.findByUserId(userId)
                .orElse(UserNotificationPreference.builder()
                        .userId(userId)
                        .email("")
                        .monthlyEmailEnabled(true)
                        .build());
    }
}