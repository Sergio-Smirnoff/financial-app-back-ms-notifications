package com.financialapp.notifications.domain.messaging;

import com.financialapp.notifications.domain.model.entity.Notification;

public interface InAppNotificationSender {
    void sendToUser(Long userId, Notification notification);
}

//ojo emitter