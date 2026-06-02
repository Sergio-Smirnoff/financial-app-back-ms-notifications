package com.financialapp.notifications.domain.usecase.notification.command;

public record MarkOneAsReadCommand(Long userId, Long notificationId) {
}
