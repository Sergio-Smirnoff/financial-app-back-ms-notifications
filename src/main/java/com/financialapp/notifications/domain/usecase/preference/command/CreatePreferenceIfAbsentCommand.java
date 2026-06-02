package com.financialapp.notifications.domain.usecase.preference.command;

public record CreatePreferenceIfAbsentCommand(Long userId, String email) {
}
