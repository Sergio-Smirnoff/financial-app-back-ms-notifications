package com.financialapp.notifications.domain.usecase.preference;

import com.financialapp.notifications.domain.usecase.preference.command.CreatePreferenceIfAbsentCommand;

public interface CreatePreferenceIfAbsentUseCase {
    void execute(CreatePreferenceIfAbsentCommand command);
}
