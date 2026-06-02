package com.financialapp.notifications.application.usecase.notification.impl;

import java.util.List;

import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.usecase.notification.GetLatestNotificationsByBankUseCase;
import com.financialapp.notifications.domain.usecase.notification.command.GetLatestNotificationsByBankCommand;
import com.financialapp.notifications.domain.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetLatestNotificationsByBankUseCaseImpl implements GetLatestNotificationsByBankUseCase {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public List<Notification> execute(GetLatestNotificationsByBankCommand command) {

        return notificationRepository.findLatestByBank(command.userId(), command.bankId().toString());
    }
}
