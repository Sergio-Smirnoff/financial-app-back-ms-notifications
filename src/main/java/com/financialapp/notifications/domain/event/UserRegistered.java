package com.financialapp.notifications.domain.event;

public record UserRegistered(
        Long userId,
        String email,
        String firstName,
        String lastName
) {}
