package com.financialapp.notifications.application.usecase.notification.impl;

import com.financialapp.notifications.domain.exception.BusinessException;
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
        if (command.userId() == null || command.userId() <= 0) {
            throw new BusinessException("userId must be a positive number");
        }
        if (command.page() < 0) {
            throw new BusinessException("Page number must be greater than or equal to 0");
        }
        if (command.size() <= 0 || command.size() > 100) {
            throw new BusinessException("Page size must be between 1 and 100");
        }

        return notificationRepository.findByUserIdOrderByCreatedAtDesc(command.userId(), command.page(),
                command.size());
    }
}
