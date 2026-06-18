package com.financialapp.notifications.infrastructure.sse;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SseHeartbeatScheduler {

    private final SseInAppNotificationSender sseInAppNotificationSender;

    @Scheduled(fixedRateString = "${notifications.sse.heartbeat-ms:15000}")
    public void sendHeartbeats() {
        sseInAppNotificationSender.sendHeartbeat();
    }
}
