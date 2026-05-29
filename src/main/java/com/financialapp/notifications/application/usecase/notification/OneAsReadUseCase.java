package com.financialapp.notifications.application.usecase.notification;

import com.financialapp.notifications.domain.model.entity.Notification;
import com.financialapp.notifications.domain.model.exception.ResourceNotFoundException;
import com.financialapp.notifications.infrastructure.repository.NotificationRepository;
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
                .filter(n -> n.getUserId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Notification", notificationId));
        notification.setRead(true);
    }

}
