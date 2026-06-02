package com.financialapp.notifications.application.usecase.notification;

import com.financialapp.notifications.domain.repository.NotificationRepository;
import com.financialapp.notifications.domain.usecase.notification.GetUnreadCountUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetUnreadCountUseCaseImpl implements GetUnreadCountUseCase {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public long execute(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }
}
