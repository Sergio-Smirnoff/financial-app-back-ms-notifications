package com.financialapp.notifications.application.usecase.preference.impl;

import com.financialapp.notifications.domain.exception.BusinessException;
import com.financialapp.notifications.domain.model.notification.NotificationCategory;
import com.financialapp.notifications.domain.model.notification.NotificationPreference;
import com.financialapp.notifications.domain.repository.NotificationPreferenceRepository;
import com.financialapp.notifications.domain.usecase.preference.UpdateCategoryPreferenceUseCase;
import com.financialapp.notifications.domain.usecase.preference.command.UpdateCategoryPreferenceCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateCategoryPreferenceUseCaseImpl implements UpdateCategoryPreferenceUseCase {

    private final NotificationPreferenceRepository preferenceRepository;

    @Override
    @Transactional
    public NotificationPreference execute(UpdateCategoryPreferenceCommand command) {
        NotificationCategory category;
        try {
            category = NotificationCategory.valueOf(command.category().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new BusinessException("Invalid notification category: " + command.category());
        }

        NotificationPreference preference = preferenceRepository.findByUserIdAndCategory(command.userId(), category)
                .orElseGet(() -> NotificationPreference.defaults(command.userId(), category));

        NotificationPreference updated = preference.withChannels(command.inAppEnabled(), command.emailEnabled());
        return preferenceRepository.save(updated);
    }
}
