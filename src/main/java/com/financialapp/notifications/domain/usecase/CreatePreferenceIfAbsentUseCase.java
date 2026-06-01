package com.financialapp.notifications.domain.usecase;

public interface CreatePreferenceIfAbsentUseCase {
    void execute(Long userId, String email);
}