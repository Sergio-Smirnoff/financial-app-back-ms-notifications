package com.financialapp.notifications.domain.usecase.preference;

public interface CreatePreferenceIfAbsentUseCase {
    void execute(Long userId, String email);
}