package com.financialapp.notifications.domain.usecase.notification;

public interface OneAsReadUsecase {
    void execute(Long userId, Long notificationId);
}
