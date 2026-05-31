package com.financialapp.notifications.domain.messaging;

import com.financialapp.notifications.domain.model.entity.Notification;
import com.financialapp.notifications.domain.model.response.NotificationResponse;

public interface InAppNotificationSender {
    void sendToUser(Long userId, Notification notification);
}

//ojo emitter