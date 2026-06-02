package com.financialapp.notifications.application.usecase.preference;

import com.financialapp.notifications.domain.repository.UserNotificationPreferenceRepository;
import com.financialapp.notifications.domain.model.notification.UserNotificationPreference;
import com.financialapp.notifications.domain.usecase.preference.CreatePreferenceIfAbsentUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreatePreferenceIfAbsentUseCaseImpl implements CreatePreferenceIfAbsentUseCase {

    private final UserNotificationPreferenceRepository preferenceRepository;

    @Transactional
    public void execute(Long userId, String email) {
        if (preferenceRepository.findByUserId(userId).isEmpty()) {
            preferenceRepository.save(UserNotificationPreference.create(userId, email));
            log.info("Created notification preferences for userId={}", userId);
        }
    }
}