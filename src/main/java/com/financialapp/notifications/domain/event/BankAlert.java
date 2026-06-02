package com.financialapp.notifications.domain.event;

public record BankAlert(
        Long userId,
        String type,
        String title,
        String message,
        String metadata
) {}
