package com.financialapp.notifications.application.usecase.preference;

import com.financialapp.notifications.domain.exception.UserNotFoundException;
import com.financialapp.notifications.domain.repository.UserNotificationPreferenceRepository;
import com.financialapp.notifications.domain.model.notification.UserNotificationPreference;
import com.financialapp.notifications.domain.usecase.preference.GetPreferenceUseCase;
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
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}