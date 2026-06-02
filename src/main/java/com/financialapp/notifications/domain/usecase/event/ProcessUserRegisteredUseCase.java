package com.financialapp.notifications.domain.usecase.event;

import com.financialapp.notifications.domain.event.UserRegistered;

public interface ProcessUserRegisteredUseCase {
    void execute(UserRegistered user);
}
