package com.financialapp.notifications.domain.event;

public record CardExpiring(
        Long userId,
        String cardNumber,
        String bankNumber,
        String expiringDate
) {}
