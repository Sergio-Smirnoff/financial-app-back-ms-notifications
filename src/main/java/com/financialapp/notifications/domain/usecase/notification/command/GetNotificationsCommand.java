package com.financialapp.notifications.domain.usecase.notification.command;

public record GetNotificationsCommand(Long userId, int page, int size) {
}
