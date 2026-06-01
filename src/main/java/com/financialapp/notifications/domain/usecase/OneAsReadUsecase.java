package com.financialapp.notifications.domain.usecase;

public interface OneAsReadUsecase {
    void execute(Long userId, Long notificationId);
}
