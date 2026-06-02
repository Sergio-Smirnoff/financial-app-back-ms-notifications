package com.financialapp.notifications.application.usecase.preference.impl;

import com.financialapp.notifications.domain.repository.UserNotificationPreferenceRepository;
import com.financialapp.notifications.domain.model.notification.UserNotificationPreference;
import com.financialapp.notifications.domain.usecase.preference.CreatePreferenceIfAbsentUseCase;
import com.financialapp.notifications.domain.usecase.preference.command.CreatePreferenceIfAbsentCommand;
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
    public void execute(CreatePreferenceIfAbsentCommand command) {
        if (preferenceRepository.findByUserId(command.userId()).isEmpty()) {
            preferenceRepository.save(UserNotificationPreference.create(command.userId(), command.email()));
            log.info("Created notification preferences for userId={}", command.userId());
        }
    }
}
