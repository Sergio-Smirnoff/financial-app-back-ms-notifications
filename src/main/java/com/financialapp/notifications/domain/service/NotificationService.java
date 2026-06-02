package com.financialapp.notifications.domain.service;

import com.financialapp.notifications.domain.model.notification.Notification;

public interface NotificationService {
    void notify(Notification notification);
}
