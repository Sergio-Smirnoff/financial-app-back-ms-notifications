package com.financialapp.notifications.application.usecase.notification;

import com.financialapp.notifications.domain.model.entity.Notification;
import com.financialapp.notifications.domain.model.response.NotificationResponse;
import com.financialapp.notifications.domain.model.response.PageResult;
import com.financialapp.notifications.domain.repository.NotificationRepository;
import com.financialapp.notifications.domain.usecase.GetNotificationUseCase;
import com.financialapp.notifications.web.controller.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetNotificationUseCaseImpl implements GetNotificationUseCase {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Transactional(readOnly = true)
    public PageResult<NotificationResponse> execute(Long userId, int page, int size) {
        PageResult<Notification> pageResult = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, page, size);
        return new PageResult<>(
                pageResult.content().stream()
                        .map(notificationMapper::toResponse)
                        .toList(),
                pageResult.pageNumber(),
                pageResult.pageSize(),
                pageResult.totalElements(),
                pageResult.totalPages()
        );
    }
}
