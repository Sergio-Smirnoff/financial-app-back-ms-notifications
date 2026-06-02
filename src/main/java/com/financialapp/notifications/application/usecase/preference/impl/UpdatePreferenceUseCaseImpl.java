package com.financialapp.notifications.application.usecase.preference.impl;

import com.financialapp.notifications.domain.exception.UserNotFoundException;
import com.financialapp.notifications.domain.repository.UserNotificationPreferenceRepository;
import com.financialapp.notifications.domain.model.notification.UserNotificationPreference;
import com.financialapp.notifications.domain.usecase.preference.UpdatePreferenceUseCase;
import com.financialapp.notifications.domain.usecase.preference.command.UpdatePreferenceCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdatePreferenceUseCaseImpl implements UpdatePreferenceUseCase {

    private final UserNotificationPreferenceRepository preferenceRepository;

    @Transactional
    public UserNotificationPreference execute(UpdatePreferenceCommand command) {
        UserNotificationPreference preference = preferenceRepository.findByUserId(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));

        preference = preference.withMonthlyEmailEnabled(command.monthlyEmailEnabled());
        return preferenceRepository.save(preference);
    }
}
