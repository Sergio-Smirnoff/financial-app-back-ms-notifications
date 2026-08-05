package com.financialapp.notifications.infrastructure.messaging.listener;

import com.financialapp.commons.messaging.infrastructure.messaging.consume.IdempotentEventProcessor;
import com.financialapp.notifications.domain.usecase.event.ProcessBudgetThresholdUseCase;
import com.financialapp.notifications.domain.usecase.event.command.ProcessBudgetThresholdCommand;
import com.financialapp.notifications.infrastructure.messaging.mapper.BudgetThresholdMapper;
import com.financialapp.notifications.infrastructure.messaging.payload.BudgetThresholdReachedData;
import io.cloudevents.CloudEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FinancesEventListener {

    private final ProcessBudgetThresholdUseCase useCase;
    private final IdempotentEventProcessor processor;

    @KafkaListener(topics = "finances.budget.threshold_reached", groupId = "notifications-group")
    public void handleBudgetThresholdReached(CloudEvent event) {
        log.info("Received finances.budget.threshold_reached event id={}", event.getId());
        processor.process(event, BudgetThresholdReachedData.class,
                data -> useCase.execute(new ProcessBudgetThresholdCommand(BudgetThresholdMapper.toDomain(data))));
    }
}
