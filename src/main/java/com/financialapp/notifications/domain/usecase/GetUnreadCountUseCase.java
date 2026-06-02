package com.financialapp.notifications.domain.usecase;

public interface GetUnreadCountUseCase {
    long execute(Long userId);
}
