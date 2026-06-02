package com.financialapp.notifications.infrastructure.kafka.mapper;

import com.financialapp.notifications.domain.event.InstallmentReminder;
import com.financialapp.notifications.infrastructure.kafka.event.InstallmentReminderEvent;
import org.springframework.stereotype.Component;

@Component
public class InstallmentReminderMapper {

    public static InstallmentReminder toDomain(InstallmentReminderEvent event) {
        InstallmentReminderEvent.Payload payload = event.getPayload();

        return new InstallmentReminder(
                event.getUserId(),
                payload.getLoanId(),
                payload.getInstallmentId(),
                payload.getLoanDescription(),
                payload.getInstallmentNumber(),
                payload.getDueDate(),
                payload.getAmount(),
                payload.getCurrency()
        );
    }
}