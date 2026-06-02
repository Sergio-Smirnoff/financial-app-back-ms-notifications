package com.financialapp.notifications.domain.usecase.notification;

import java.util.List;

import com.financialapp.notifications.domain.model.notification.Notification;

public interface GetLatestNotificationsUseCase {
    List<Notification> execute(Long userId, Long bankId);
}
