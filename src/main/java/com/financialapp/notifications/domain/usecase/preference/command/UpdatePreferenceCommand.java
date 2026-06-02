package com.financialapp.notifications.domain.usecase.preference.command;

public record UpdatePreferenceCommand(Long userId, boolean monthlyEmailEnabled) {
}
