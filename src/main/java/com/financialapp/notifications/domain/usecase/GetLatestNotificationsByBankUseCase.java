package com.financialapp.notifications.domain.usecase;

import java.util.List;

import com.financialapp.notifications.domain.model.entity.Notification;

public interface GetLatestNotificationsByBankUseCase {
    public List<Notification> execute(Long userId, Long bankId);
}
