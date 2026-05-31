package com.financialapp.notifications.application.usecase.event;

import com.financialapp.notifications.application.service.NotificationService;
import com.financialapp.notifications.domain.interfaces.usecase.event.ProcessUserRegisteredUseCase;
import com.financialapp.notifications.domain.model.entity.event.UserRegistered;
import com.financialapp.notifications.domain.model.entity.enums.NotificationChannel;
import com.financialapp.notifications.domain.model.entity.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProcessUserRegisteredUseCaseImpl implements ProcessUserRegisteredUseCase {

    private final NotificationService notificationService;

    @Override
    @Transactional
    public void execute(UserRegistered user) {
        // create preferences
        notificationService.createPreferenceIfAbsent(user.userId(), user.email());

        String title = "Welcome to Financial App!";
        String message = String.format(
                "Hi %s, your account has been created successfully. Start tracking your finances, investments, and more.",
                user.firstName());

        notificationService.notify(
                user.userId(), NotificationType.USER_REGISTERED, title, message,
                NotificationChannel.BOTH, null);
    }


}
