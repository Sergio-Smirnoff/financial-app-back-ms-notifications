package com.financialapp.notifications.infrastructure.gateway;

import com.financialapp.commons.messaging.domain.gateway.ProcessedEventGateway;
import com.financialapp.commons.messaging.domain.model.EventId;
import com.financialapp.notifications.infrastructure.persistence.entity.ProcessedEventSqlEntity;
import com.financialapp.notifications.infrastructure.persistence.repository.ProcessedEventSqlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ProcessedEventGatewayJpaAdapter implements ProcessedEventGateway {

    private final ProcessedEventSqlRepository repository;

    @Override
    public boolean isProcessed(EventId eventId) {
        return repository.existsById(eventId.value());
    }

    @Override
    public void markProcessed(EventId eventId) {
        ProcessedEventSqlEntity entity = new ProcessedEventSqlEntity();
        entity.setEventId(eventId.value());
        entity.setProcessedAt(LocalDateTime.now());
        repository.save(entity);
    }
}
