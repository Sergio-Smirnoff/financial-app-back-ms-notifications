package com.financialapp.notifications.infrastructure.kafka.listener;

import com.financialapp.notifications.domain.interfaces.usecase.event.ProcessInvestmentThresholdUseCase;
import com.financialapp.notifications.infrastructure.kafka.event.InvestmentThresholdEvent;
import com.financialapp.notifications.infrastructure.kafka.mapper.InvestmentThresholdMapper;
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

        useCase.execute(InvestmentThresholdMapper.toDomain(event));
    }
}
