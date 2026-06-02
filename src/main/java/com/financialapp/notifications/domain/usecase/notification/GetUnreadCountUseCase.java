package com.financialapp.notifications.domain.usecase.notification;

public interface GetUnreadCountUseCase {
    long execute(Long userId);
}
