package com.financialapp.notifications.domain.usecase.preference.command;

public record UpdateCategoryPreferenceCommand(
        Long userId,
        String category,
        boolean inAppEnabled,
        boolean emailEnabled
) {
}
