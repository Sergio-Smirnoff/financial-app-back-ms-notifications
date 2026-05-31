package com.financialapp.notifications.application.usecase.notification;

import com.financialapp.notifications.domain.model.entity.Notification;
import com.financialapp.notifications.domain.model.exception.ResourceNotFoundException;
import com.financialapp.notifications.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OneAsReadUseCase {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void execute(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .filter(n -> n.userId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Notification", notificationId));
        notificationRepository.save(notification.markAsRead());
    }

}
