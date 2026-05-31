package com.financialapp.notifications.domain.model.entity.event;

import lombok.Builder;

@Builder
public record UserRegistered(
        Long userId,
        String email,
        String firstName,
        String lastName
) {}
