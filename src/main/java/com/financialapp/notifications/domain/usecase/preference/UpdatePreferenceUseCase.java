package com.financialapp.notifications.domain.usecase.preference;

import com.financialapp.notifications.domain.model.notification.UserNotificationPreference;
import com.financialapp.notifications.domain.usecase.preference.command.UpdatePreferenceCommand;

public interface UpdatePreferenceUseCase {
    UserNotificationPreference execute(UpdatePreferenceCommand command);
}
