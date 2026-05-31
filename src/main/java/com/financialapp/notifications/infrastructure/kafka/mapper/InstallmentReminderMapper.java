package com.financialapp.notifications.infrastructure.kafka.mapper;

import com.financialapp.notifications.domain.model.entity.event.InstallmentReminder;
import com.financialapp.notifications.infrastructure.kafka.event.InstallmentReminderEvent;
import org.springframework.stereotype.Component;

@Component
public class InstallmentReminderMapper {

    public static InstallmentReminder toDomain(InstallmentReminderEvent event) {
        InstallmentReminderEvent.Payload payload = event.getPayload();

        return InstallmentReminder.builder()
                .userId(event.getUserId())
                .loanId(payload.getLoanId())
                .installmentId(payload.getInstallmentId())
                .loanDescription(payload.getLoanDescription())
                .installmentNumber(payload.getInstallmentNumber())
                .dueDate(payload.getDueDate())
                .amount(payload.getAmount())
                .currency(payload.getCurrency())
                .build();
    }
}