package com.financialapp.notifications.application.usecase.notification;

import com.financialapp.notifications.domain.repository.NotificationRepository;
import com.financialapp.notifications.domain.usecase.notification.AllAsReadUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AllAsReadUseCaseImpl implements AllAsReadUseCase {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void execute(Long userId) {
        notificationRepository.markAllAsRead(userId);
    }

}
