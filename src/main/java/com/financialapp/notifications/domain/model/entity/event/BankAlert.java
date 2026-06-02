package com.financialapp.notifications.domain.model.entity.event;

public record BankAlert(
        Long userId,
        String type,
        String title,
        String message,
        String metadata
) {}
