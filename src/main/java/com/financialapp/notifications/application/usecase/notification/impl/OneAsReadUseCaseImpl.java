package com.financialapp.notifications.application.usecase.notification.impl;

import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.exception.BusinessException;
import com.financialapp.notifications.domain.exception.ResourceNotFoundException;
import com.financialapp.notifications.domain.repository.NotificationRepository;
import com.financialapp.notifications.domain.usecase.notification.OneAsReadUsecase;
import com.financialapp.notifications.domain.usecase.notification.command.MarkOneAsReadCommand;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OneAsReadUseCaseImpl implements OneAsReadUsecase {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void execute(MarkOneAsReadCommand command) {
        if (command.userId() == null || command.userId() <= 0) {
            throw new BusinessException("userId must be a positive number");
        }
        Notification notification = notificationRepository.findById(command.notificationId())
                .filter(n -> n.userId().equals(command.userId()))
                .orElseThrow(() -> new ResourceNotFoundException("Notification", command.notificationId()));
        notificationRepository.save(notification.markAsRead());
    }

}
