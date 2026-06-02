package com.financialapp.notifications.domain.usecase.preference;

import com.financialapp.notifications.domain.model.notification.UserNotificationPreference;
import com.financialapp.notifications.domain.usecase.preference.command.GetPreferenceCommand;

public interface GetPreferenceUseCase {
    UserNotificationPreference execute(GetPreferenceCommand command);
}
