package com.financialapp.notifications.application.usecase.preference.impl;

import com.financialapp.notifications.domain.model.notification.NotificationCategory;
import com.financialapp.notifications.domain.model.notification.NotificationPreference;
import com.financialapp.notifications.domain.repository.NotificationPreferenceRepository;
import com.financialapp.notifications.domain.usecase.preference.GetPreferencesByCategoryUseCase;
import com.financialapp.notifications.domain.usecase.preference.command.GetPreferencesByCategoryCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetPreferencesByCategoryUseCaseImpl implements GetPreferencesByCategoryUseCase {

    private final NotificationPreferenceRepository preferenceRepository;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationPreference> execute(GetPreferencesByCategoryCommand command) {
        List<NotificationPreference> result = new ArrayList<>();
        for (NotificationCategory category : NotificationCategory.values()) {
            NotificationPreference pref = preferenceRepository.findByUserIdAndCategory(command.userId(), category)
                    .orElseGet(() -> NotificationPreference.defaults(command.userId(), category));
            result.add(pref);
        }
        return result;
    }
}
