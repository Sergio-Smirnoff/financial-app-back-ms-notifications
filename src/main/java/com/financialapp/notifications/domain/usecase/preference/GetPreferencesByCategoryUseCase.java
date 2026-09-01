package com.financialapp.notifications.domain.usecase.preference;

import com.financialapp.notifications.domain.model.notification.NotificationPreference;
import com.financialapp.notifications.domain.usecase.preference.command.GetPreferencesByCategoryCommand;

import java.util.List;

public interface GetPreferencesByCategoryUseCase {
    List<NotificationPreference> execute(GetPreferencesByCategoryCommand command);
}
