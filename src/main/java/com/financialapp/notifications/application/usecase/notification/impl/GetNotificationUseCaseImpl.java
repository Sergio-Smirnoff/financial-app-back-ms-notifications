package com.financialapp.notifications.application.usecase.notification.impl;

import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.pagination.PageResult;
import com.financialapp.notifications.domain.repository.NotificationRepository;
import com.financialapp.notifications.domain.usecase.notification.GetNotificationUseCase;
import com.financialapp.notifications.domain.usecase.notification.command.GetNotificationsCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetNotificationUseCaseImpl implements GetNotificationUseCase {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public PageResult<Notification> execute(GetNotificationsCommand command) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(command.userId(), command.page(), command.size());
    }
}
