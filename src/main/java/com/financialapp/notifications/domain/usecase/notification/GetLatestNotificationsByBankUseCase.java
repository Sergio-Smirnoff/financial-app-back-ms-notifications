package com.financialapp.notifications.domain.usecase.notification;

import java.util.List;

import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.usecase.notification.command.GetLatestNotificationsByBankCommand;

public interface GetLatestNotificationsByBankUseCase {
    List<Notification> execute(GetLatestNotificationsByBankCommand command);
}
