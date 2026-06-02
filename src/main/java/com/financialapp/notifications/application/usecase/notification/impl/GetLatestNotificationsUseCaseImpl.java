package com.financialapp.notifications.application.usecase.notification.impl;

import java.util.List;

import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.repository.NotificationRepository;
import com.financialapp.notifications.domain.usecase.notification.GetLatestNotificationsUseCase;
import com.financialapp.notifications.domain.usecase.notification.command.GetLatestNotificationsCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetLatestNotificationsUseCaseImpl implements GetLatestNotificationsUseCase {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public List<Notification> execute(GetLatestNotificationsCommand command) {

        return notificationRepository.findTop5ByUserIdOrderByCreatedAtDesc(command.userId());
    }
}
