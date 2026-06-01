package com.financialapp.notifications.domain.usecase;

import com.financialapp.notifications.domain.model.response.UnreadCountResponse;

public interface GetUnreadCountUseCase {
    public UnreadCountResponse execute(Long userId);
}
