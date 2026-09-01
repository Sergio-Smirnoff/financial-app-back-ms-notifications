package com.financialapp.notifications.domain.usecase.preference;

import com.financialapp.notifications.domain.model.notification.NotificationPreference;
import com.financialapp.notifications.domain.usecase.preference.command.UpdateCategoryPreferenceCommand;

public interface UpdateCategoryPreferenceUseCase {
    NotificationPreference execute(UpdateCategoryPreferenceCommand command);
}
