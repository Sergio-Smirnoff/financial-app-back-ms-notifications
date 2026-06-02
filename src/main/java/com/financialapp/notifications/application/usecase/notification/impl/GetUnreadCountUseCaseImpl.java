package com.financialapp.notifications.application.usecase.notification.impl;

import com.financialapp.notifications.domain.repository.NotificationRepository;
import com.financialapp.notifications.domain.usecase.notification.GetUnreadCountUseCase;
import com.financialapp.notifications.domain.usecase.notification.command.GetUnreadCountCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetUnreadCountUseCaseImpl implements GetUnreadCountUseCase {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public long execute(GetUnreadCountCommand command) {
        return notificationRepository.countByUserIdAndReadFalse(command.userId());
    }
}
