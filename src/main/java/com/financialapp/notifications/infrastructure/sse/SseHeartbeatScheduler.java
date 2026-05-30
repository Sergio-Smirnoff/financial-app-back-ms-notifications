package com.financialapp.notifications.infrastructure.sse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SseHeartbeatScheduler {

    private static final String HEARTBEAT_EVENT = ":heartbeat\n\n";

    private final SseInAppNotificationSender sseInAppNotificationSender;

    @Async
    @Scheduled(fixedRate = 30000)
    public void sendHeartbeats() {
        sseInAppNotificationSender.sendHeartbeat();
    }
}
