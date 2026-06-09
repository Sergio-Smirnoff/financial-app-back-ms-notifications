package com.financialapp.notifications.infrastructure.messaging.listener;

import com.financialapp.notifications.domain.usecase.event.ProcessUserRegisteredUseCase;
import com.financialapp.notifications.domain.usecase.event.command.ProcessUserRegisteredCommand;
import com.financialapp.notifications.infrastructure.messaging.mapper.UserRegisteredMapper;
import com.financialapp.notifications.infrastructure.messaging.payload.UserRegisteredData;
import com.financialapp.commons.messaging.infrastructure.messaging.consume.IdempotentEventProcessor;
import io.cloudevents.CloudEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventListener {

    private final ProcessUserRegisteredUseCase useCase;
    private final IdempotentEventProcessor processor;

    @KafkaListener(topics = "users.user.registered", groupId = "notifications-group")
    public void handleUserRegistered(CloudEvent event) {
        log.info("Received users.user.registered event id={}", event.getId());
        processor.process(event, UserRegisteredData.class,
                data -> useCase.execute(new ProcessUserRegisteredCommand(UserRegisteredMapper.toDomain(data))));
    }
}
