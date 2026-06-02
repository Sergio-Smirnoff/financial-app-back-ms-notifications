package com.financialapp.notifications.application.usecase.event;

import com.financialapp.notifications.application.service.NotificationService;
import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.event.UserRegistered;
import com.financialapp.notifications.domain.usecase.preference.CreatePreferenceIfAbsentUseCase;
import com.financialapp.notifications.domain.usecase.event.ProcessUserRegisteredUseCase;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProcessUserRegisteredUseCaseImpl implements ProcessUserRegisteredUseCase {

    private final NotificationService notificationService;
    private final CreatePreferenceIfAbsentUseCase createPreferenceIfAbsentUseCase;

    @Override
    @Transactional
    public void execute(UserRegistered user) {
        // create preferences
        createPreferenceIfAbsentUseCase.execute(user.userId(), user.email());

        String title = "Welcome to Financial App!";
        String message = String.format(
                "Hi %s, your account has been created successfully. Start tracking your finances, investments, and more.",
                user.firstName());

        var newNotification = Notification.create(
                user.userId(), NotificationType.USER_REGISTERED, title, message,
                NotificationChannel.BOTH, null);
        notificationService.notify(newNotification);
    }

}
