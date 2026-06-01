package com.financialapp.notifications.infrastructure.kafka.listener;

import com.financialapp.notifications.domain.usecase.event.ProcessBankEventUseCase;
import com.financialapp.notifications.infrastructure.kafka.event.BankAlertEvent;
import com.financialapp.notifications.infrastructure.kafka.mapper.BankAlertMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BankEventListener {

    private final ProcessBankEventUseCase useCase;

    @KafkaListener(topics = "bank-alerts", groupId = "notifications-group")
    public void handleBankAlert(BankAlertEvent event) {
        log.info("Received bank-alert event of type {} for userId={}", event.getType(), event.getUserId());

        useCase.execute(BankAlertMapper.toDomain(event));
    }
}
