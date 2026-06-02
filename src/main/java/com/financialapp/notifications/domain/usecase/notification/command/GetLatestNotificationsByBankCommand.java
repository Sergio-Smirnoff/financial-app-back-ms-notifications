package com.financialapp.notifications.domain.usecase.notification.command;

public record GetLatestNotificationsByBankCommand(Long userId, Long bankId) {
}
