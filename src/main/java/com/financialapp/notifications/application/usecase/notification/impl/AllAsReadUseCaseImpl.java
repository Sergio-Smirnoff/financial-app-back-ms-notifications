package com.financialapp.notifications.application.usecase.notification.impl;

import com.financialapp.notifications.domain.repository.NotificationRepository;
import com.financialapp.notifications.domain.usecase.notification.AllAsReadUseCase;
import com.financialapp.notifications.domain.usecase.notification.command.AllAsReadCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AllAsReadUseCaseImpl implements AllAsReadUseCase {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void execute(AllAsReadCommand command) {
        notificationRepository.markAllAsRead(command.userId());
    }

}
