package com.financialapp.notifications.domain.interfaces.usecase.event;

import com.financialapp.notifications.domain.model.entity.event.UserRegistered;

public interface ProcessUserRegisteredUseCase {
    void execute(UserRegistered user);
}
