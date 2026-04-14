package com.financialapp.notifications.scheduler;

import com.financialapp.notifications.service.SseEmitterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@RequiredArgsConstructor
@Slf4j
public class SseHeartbeatScheduler {

    private static final String HEARTBEAT_EVENT = ":heartbeat\n\n";

    private final SseEmitterService sseEmitterService;

    @Async
    @Scheduled(fixedRate = 30000)
    public void sendHeartbeats() {
        sseEmitterService.sendHeartbeat();
    }
}
