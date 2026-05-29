package com.financialapp.notifications.application.usecase.notification;

import java.util.List;

import com.financialapp.notifications.domain.model.entity.Notification;
import com.financialapp.notifications.domain.model.response.NotificationResponse;
import com.financialapp.notifications.infrastructure.repository.NotificationRepository;
import com.financialapp.notifications.web.controller.mapper.NotificationMapper;

@Service
@RequiredArgsConstructor
public class GetLatestNotificationsUseCase {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Transactional(readOnly = true)
    public List<NotificationResponse> execute(Long userId, Long bankId) {

        List<Notification> notifications = bankId != null
                ? notificationRepository.findLatestByBank(userId, bankId.toString())
                : notificationRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId);

        return notifications
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }
}
