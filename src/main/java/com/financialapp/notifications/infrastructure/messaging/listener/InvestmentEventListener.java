package com.financialapp.notifications.infrastructure.messaging.listener;

import com.financialapp.notifications.domain.usecase.event.ProcessInvestmentThresholdUseCase;
import com.financialapp.notifications.domain.usecase.event.command.ProcessInvestmentThresholdCommand;
import com.financialapp.notifications.infrastructure.messaging.payload.InvestmentThresholdEvent;
import com.financialapp.notifications.infrastructure.messaging.mapper.InvestmentThresholdMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InvestmentEventListener {

    private final ProcessInvestmentThresholdUseCase useCase;

    @KafkaListener(topics = "investment.threshold.reached", groupId = "notifications-group")
    public void handleThresholdReached(InvestmentThresholdEvent event) {
        log.info("Received investment.threshold.reached event for userId={}", event.getUserId());

        useCase.execute(new ProcessInvestmentThresholdCommand(InvestmentThresholdMapper.toDomain(event)));
    }
}
