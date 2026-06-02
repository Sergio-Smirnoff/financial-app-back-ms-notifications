package com.financialapp.notifications.application.usecase.notification;

import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.pagination.PageResult;
import com.financialapp.notifications.domain.repository.NotificationRepository;
import com.financialapp.notifications.domain.usecase.GetNotificationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetNotificationUseCaseImpl implements GetNotificationUseCase {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public PageResult<Notification> execute(Long userId, int page, int size) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, page, size);
    }
}
