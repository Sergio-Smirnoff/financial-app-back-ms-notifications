package com.financialapp.notifications.application.usecase.preference.impl;

import com.financialapp.notifications.domain.exception.UserNotFoundException;
import com.financialapp.notifications.domain.model.notification.NotificationCategory;
import com.financialapp.notifications.domain.model.notification.NotificationPreference;
import com.financialapp.notifications.domain.model.notification.UserNotificationPreference;
import com.financialapp.notifications.domain.repository.NotificationPreferenceRepository;
import com.financialapp.notifications.domain.repository.UserNotificationPreferenceRepository;
import com.financialapp.notifications.domain.usecase.preference.GetPreferenceUseCase;
import com.financialapp.notifications.domain.usecase.preference.command.GetPreferenceCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetPreferenceUseCaseImpl implements GetPreferenceUseCase {

    private final NotificationPreferenceRepository preferenceRepository;
    private final UserNotificationPreferenceRepository userNotificationPreferenceRepository;

    @Override
    @Transactional(readOnly = true)
    public UserNotificationPreference execute(GetPreferenceCommand command) {
        var oldPrefOpt = userNotificationPreferenceRepository.findByUserId(command.userId());
        var newSummaryOpt = preferenceRepository.findByUserIdAndCategory(command.userId(), NotificationCategory.SUMMARY);

        if (oldPrefOpt.isEmpty() && newSummaryOpt.isEmpty()) {
            throw new UserNotFoundException(command.userId());
        }

        NotificationPreference summaryPref = newSummaryOpt.orElseGet(() ->
                preferenceRepository.save(NotificationPreference.defaults(command.userId(), NotificationCategory.SUMMARY))
        );

        String email = oldPrefOpt.map(UserNotificationPreference::email).orElse("user@financialapp.com");

        return new UserNotificationPreference(
                summaryPref.id() != null ? summaryPref.id() : 1L,
                command.userId(),
                email,
                summaryPref.emailEnabled(),
                summaryPref.createdAt(),
                summaryPref.updatedAt()
        );
    }
}
