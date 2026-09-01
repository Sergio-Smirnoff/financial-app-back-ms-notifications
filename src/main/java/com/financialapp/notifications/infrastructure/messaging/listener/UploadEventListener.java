package com.financialapp.notifications.infrastructure.messaging.listener;

import com.financialapp.commons.messaging.infrastructure.messaging.consume.IdempotentEventProcessor;
import com.financialapp.notifications.domain.usecase.event.ProcessImportStaleUseCase;
import com.financialapp.notifications.domain.usecase.event.command.ProcessImportStaleCommand;
import com.financialapp.notifications.infrastructure.messaging.mapper.ImportStaleMapper;
import com.financialapp.notifications.infrastructure.messaging.payload.ImportStaleData;
import io.cloudevents.CloudEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UploadEventListener {

    private final ProcessImportStaleUseCase useCase;
    private final IdempotentEventProcessor processor;

    @KafkaListener(topics = "upload.import.stale", groupId = "notifications-group")
    public void handleImportStale(CloudEvent event) {
        log.info("Received upload.import.stale event id={}", event.getId());
        processor.process(event, ImportStaleData.class,
                data -> useCase.execute(new ProcessImportStaleCommand(ImportStaleMapper.toDomain(data))));
    }
}
