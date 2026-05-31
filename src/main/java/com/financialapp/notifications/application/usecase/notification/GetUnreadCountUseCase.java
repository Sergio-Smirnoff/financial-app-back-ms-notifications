package com.financialapp.notifications.application.usecase.notification;

import com.financialapp.notifications.domain.model.response.UnreadCountResponse;
import com.financialapp.notifications.domain.repository.NotificationRepository;
import com.financialapp.notifications.web.controller.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetUnreadCountUseCase {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public UnreadCountResponse execute(Long userId) {
        return UnreadCountResponse.builder()
                .count(notificationRepository.countByUserIdAndReadFalse(userId))
                .build();
    }
}
