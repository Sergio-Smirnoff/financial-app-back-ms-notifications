package com.financialapp.notifications.application.service;

import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.usecase.preference.GetPreferenceUseCase;
import com.financialapp.notifications.domain.usecase.preference.command.GetPreferenceCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationChannelResolver {

    private final GetPreferenceUseCase getPreferenceUseCase;

    public NotificationChannel resolve(Long userId) {
        try {
            var preference = getPreferenceUseCase.execute(new GetPreferenceCommand(userId));
            return preference.monthlyEmailEnabled()
                    ? NotificationChannel.BOTH
                    : NotificationChannel.IN_APP;
        } catch (Exception e) {
            return NotificationChannel.IN_APP;
        }
    }
}
