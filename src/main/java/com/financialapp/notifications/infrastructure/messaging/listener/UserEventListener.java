package com.financialapp.notifications.infrastructure.messaging.listener;

import com.financialapp.notifications.domain.usecase.event.ProcessUserRegisteredUseCase;
import com.financialapp.notifications.infrastructure.messaging.payload.UserRegisteredEvent;
import com.financialapp.notifications.infrastructure.messaging.mapper.UserRegisteredMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventListener {

    private final ProcessUserRegisteredUseCase useCase;

    @KafkaListener(topics = "user.registered", groupId = "notifications-group")
    public void handleUserRegistered(UserRegisteredEvent event) {
        log.info("Received user.registered event for userId={}", event.getUserId());

        useCase.execute(UserRegisteredMapper.toDomain(event));
    }
}
