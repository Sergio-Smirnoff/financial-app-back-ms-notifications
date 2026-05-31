package com.financialapp.notifications.domain.messaging;

import com.financialapp.notifications.domain.model.response.NotificationResponse;

public interface InAppNotificationSender {
    void sendToUser(Long userId, NotificationResponse notification);
}

//ojo emitter