package com.financialapp.notifications.domain.usecase.event;

import com.financialapp.notifications.domain.model.entity.event.BankAlert;

public interface ProcessBankEventUseCase {
    void execute(BankAlert alert);
}
