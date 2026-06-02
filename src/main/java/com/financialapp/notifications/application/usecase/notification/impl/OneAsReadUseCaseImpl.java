package com.financialapp.notifications.application.usecase.notification.impl;

import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.exception.ResourceNotFoundException;
import com.financialapp.notifications.domain.repository.NotificationRepository;
import com.financialapp.notifications.domain.usecase.notification.OneAsReadUsecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OneAsReadUseCaseImpl implements OneAsReadUsecase {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void execute(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .filter(n -> n.userId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Notification", notificationId));
        notificationRepository.save(notification.markAsRead());
    }

}
