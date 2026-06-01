package com.financialapp.notifications.application.usecase.notification;

import java.util.List;

import com.financialapp.notifications.domain.model.entity.Notification;
import com.financialapp.notifications.domain.model.response.NotificationResponse;
import com.financialapp.notifications.domain.usecase.GetLatestNotificationsByBankUseCase;
import com.financialapp.notifications.domain.repository.NotificationRepository;
import com.financialapp.notifications.web.controller.mapper.NotificationMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetLatestNotificationsByBankUseCaseImpl implements GetLatestNotificationsByBankUseCase {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Transactional(readOnly = true)
    public List<NotificationResponse> execute(Long userId, Long bankId) {

        return notificationRepository.findLatestByBank(userId, bankId.toString())
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }
}
