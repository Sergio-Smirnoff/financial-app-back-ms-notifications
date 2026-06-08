package com.financialapp.notifications.infrastructure.messaging.listener;

import com.financialapp.notifications.domain.usecase.event.ProcessInvestmentThresholdUseCase;
import com.financialapp.notifications.domain.usecase.event.command.ProcessInvestmentThresholdCommand;
import com.financialapp.notifications.infrastructure.messaging.mapper.InvestmentThresholdMapper;
import com.financialapp.notifications.infrastructure.messaging.payload.InvestmentThresholdData;
import com.financialapp.commons.messaging.infrastructure.messaging.consume.IdempotentEventProcessor;
import io.cloudevents.CloudEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InvestmentEventListener {

    private final ProcessInvestmentThresholdUseCase useCase;
    private final IdempotentEventProcessor processor;

    @KafkaListener(topics = "investments.threshold.breached", groupId = "notifications-group")
    public void handleThresholdBreached(CloudEvent event) {
        log.info("Received investments.threshold.breached event id={}", event.getId());
        processor.process(event, InvestmentThresholdData.class,
                data -> useCase.execute(new ProcessInvestmentThresholdCommand(InvestmentThresholdMapper.toDomain(data))));
    }
}
