package com.financialapp.notifications.domain.model.entity.event;

public record UserRegistered(
        Long userId,
        String email,
        String firstName,
        String lastName
) {}
