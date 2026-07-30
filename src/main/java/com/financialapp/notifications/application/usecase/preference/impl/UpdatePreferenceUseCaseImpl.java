package com.financialapp.notifications.application.usecase.preference.impl;

import com.financialapp.notifications.domain.exception.UserNotFoundException;
import com.financialapp.notifications.domain.model.notification.NotificationCategory;
import com.financialapp.notifications.domain.model.notification.NotificationPreference;
import com.financialapp.notifications.domain.model.notification.UserNotificationPreference;
import com.financialapp.notifications.domain.repository.NotificationPreferenceRepository;
import com.financialapp.notifications.domain.repository.UserNotificationPreferenceRepository;
import com.financialapp.notifications.domain.usecase.preference.UpdatePreferenceUseCase;
import com.financialapp.notifications.domain.usecase.preference.command.UpdatePreferenceCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdatePreferenceUseCaseImpl implements UpdatePreferenceUseCase {

    private final NotificationPreferenceRepository preferenceRepository;
    private final UserNotificationPreferenceRepository userNotificationPreferenceRepository;

    @Override
    @Transactional
    public UserNotificationPreference execute(UpdatePreferenceCommand command) {
        var oldPrefOpt = userNotificationPreferenceRepository.findByUserId(command.userId());
        var newSummaryOpt = preferenceRepository.findByUserIdAndCategory(command.userId(), NotificationCategory.SUMMARY);

        if (oldPrefOpt.isEmpty() && newSummaryOpt.isEmpty()) {
            throw new UserNotFoundException(command.userId());
        }

        NotificationPreference summaryPref = newSummaryOpt.orElseGet(() ->
                NotificationPreference.defaults(command.userId(), NotificationCategory.SUMMARY)
        );

        NotificationPreference updatedSummaryPref = summaryPref.withChannels(summaryPref.inAppEnabled(), command.monthlyEmailEnabled());
        NotificationPreference savedSummary = preferenceRepository.save(updatedSummaryPref);

        String email = oldPrefOpt.map(UserNotificationPreference::email).orElse("user@financialapp.com");

        return new UserNotificationPreference(
                savedSummary.id() != null ? savedSummary.id() : 1L,
                command.userId(),
                email,
                savedSummary.emailEnabled(),
                savedSummary.createdAt(),
                savedSummary.updatedAt()
        );
    }
}
