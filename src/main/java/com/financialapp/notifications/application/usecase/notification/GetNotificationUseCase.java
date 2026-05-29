package com.financialapp.notifications.application.usecase.notification;

import com.financialapp.notifications.domain.model.response.NotificationResponse;
import com.financialapp.notifications.infrastructure.repository.NotificationRepository;
import com.financialapp.notifications.web.controller.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;

import java.beans.Transient;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetNotificationUseCase {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Transactional(readOnly = true)
    public Page<NotificationResponse> execute(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(notificationMapper::toResponse);
    }
}
