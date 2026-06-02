package com.financialapp.notifications.domain.usecase.notification.command;

public record GetLatestNotificationsCommand(Long userId, Long bankId) {
}
