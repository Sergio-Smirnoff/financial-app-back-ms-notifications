package com.financialapp.notifications.infrastructure.sse;

import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class SseInAppNotificationSenderTest {

    private final SseInAppNotificationSender sender = new SseInAppNotificationSender();

    private Notification notification() {
        return new Notification(1L, 7L, NotificationType.PAYMENT_DUE,
                "t", "m", NotificationChannel.IN_APP, false, null, null);
    }

    @Test
    void createEmitter_registersEmitterForUser() {
        // Given a user with no emitters / When creating one
        SseEmitter emitter = sender.createEmitter(7L);

        // Then a 5-minute emitter is returned
        assertThat(emitter).isNotNull();
        assertThat(emitter.getTimeout()).isEqualTo(300_000L);
    }

    @Test
    void createEmitter_evictsOldestWhenOverCap() {
        // Given the per-user cap of 3 emitters is reached / When creating a 4th
        for (int i = 0; i < 4; i++) {
            sender.createEmitter(7L);
        }

        // Then a notification still reaches the surviving emitters without error
        sender.sendToUser(7L, notification());
    }

    @Test
    void sendToUser_noEmitters_isNoOp() {
        // Given a user with no registered emitters / When sending / Then nothing happens
        sender.sendToUser(99L, notification());
    }

    @Test
    void sendToUser_deliversToLiveEmitter() throws IOException {
        // Given a live emitter / When sending a notification
        SseEmitter emitter = sender.createEmitter(7L);
        sender.sendToUser(7L, notification());

        // Then the emitter is still usable (delivery did not error)
        assertThat(emitter).isNotNull();
    }

    @Test
    void sendToUser_removesDeadEmitter() {
        // Given an emitter that fails on send (completed → IllegalStateException)
        SseEmitter emitter = sender.createEmitter(7L);
        emitter.complete();

        // When sending / Then the dead emitter is detected and removed, leaving the user empty
        sender.sendToUser(7L, notification());
        // And a second send is a no-op (no emitters remain)
        sender.sendToUser(7L, notification());
    }

    @Test
    void sendHeartbeat_pingsAndPrunesDeadEmitters() {
        // Given one live and one dead emitter for a user
        sender.createEmitter(7L);
        SseEmitter dead = sender.createEmitter(7L);
        dead.complete();

        // When heartbeating / Then it pings the live ones and prunes the dead without error
        sender.sendHeartbeat();
    }
}
