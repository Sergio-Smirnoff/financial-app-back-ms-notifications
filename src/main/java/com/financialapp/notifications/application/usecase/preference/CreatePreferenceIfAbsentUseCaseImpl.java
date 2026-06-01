package com.financialapp.notifications.application.usecase.preference;

import com.financialapp.notifications.domain.interfaces.infrastructure.UserNotificationPreferenceRepository;
import com.financialapp.notifications.domain.model.entity.UserNotificationPreference;
import com.financialapp.notifications.domain.usecase.CreatePreferenceIfAbsentUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreatePreferenceIfAbsentUseCase implements CreatePreferenceIfAbsentUseCase {

    private final UserNotificationPreferenceRepository preferenceRepository;

    @Transactional
    public void execute(Long userId, String email) {
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