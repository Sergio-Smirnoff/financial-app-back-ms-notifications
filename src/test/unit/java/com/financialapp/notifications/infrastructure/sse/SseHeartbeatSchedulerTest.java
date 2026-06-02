package com.financialapp.notifications.infrastructure.sse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SseHeartbeatSchedulerTest {

    @Mock SseInAppNotificationSender sseInAppNotificationSender;
    @InjectMocks SseHeartbeatScheduler scheduler;

    @Test
    void sendHeartbeats_delegatesToSender() {
        // Given the heartbeat scheduler / When the scheduled tick fires
        scheduler.sendHeartbeats();

        // Then it delegates to the SSE sender
        verify(sseInAppNotificationSender).sendHeartbeat();
    }
}
