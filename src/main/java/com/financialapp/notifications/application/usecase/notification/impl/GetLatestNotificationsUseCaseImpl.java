package com.financialapp.notifications.application.usecase.notification.impl;

import java.util.List;

import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.repository.NotificationRepository;
import com.financialapp.notifications.domain.usecase.notification.GetLatestNotificationsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetLatestNotificationsUseCaseImpl implements GetLatestNotificationsUseCase {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public List<Notification> execute(Long userId, Long bankId) {

        return notificationRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId);
    }
}
