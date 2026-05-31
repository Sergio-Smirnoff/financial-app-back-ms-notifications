package com.financialapp.notifications.domain.interfaces.usecase.event;

import com.financialapp.notifications.domain.model.entity.event.BankAlert;

public interface ProcessBankEventUseCase {
    void execute(BankAlert alert);
}
