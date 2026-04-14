package com.financialapp.notifications.service;

import com.financialapp.notifications.model.dto.response.NotificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class SseEmitterService {

    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    private static final long EMITTER_TIMEOUT_MS = 300_000L; // 5 minutes
    private static final int MAX_EMITTERS_PER_USER = 3;

    public SseEmitter createEmitter(Long userId) {
        SseEmitter emitter = new SseEmitter(EMITTER_TIMEOUT_MS);

        List<SseEmitter> userEmitters = emitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>());

        // Enforce cap: evict oldest if over limit
        while (userEmitters.size() >= MAX_EMITTERS_PER_USER) {
            SseEmitter oldest = userEmitters.remove(0);
            try { oldest.complete(); } catch (Exception ignored) {}
        }

        userEmitters.add(emitter);

        Runnable cleanup = () -> {
            userEmitters.remove(emitter);
            if (userEmitters.isEmpty()) {
                emitters.remove(userId);
            }
        };

        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(e -> cleanup.run());

        log.debug("SSE emitter created for userId={}, total emitters={}", userId, userEmitters.size());
        return emitter;
    }

    public void sendToUser(Long userId, NotificationResponse notification) {
        List<SseEmitter> userEmitters = emitters.get(userId);
        if (userEmitters == null || userEmitters.isEmpty()) return;

        List<SseEmitter> dead = new CopyOnWriteArrayList<>();
        for (SseEmitter emitter : userEmitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(notification));
            } catch (Exception e) {
                log.debug("SSE emitter dead for userId={}, removing", userId);
                dead.add(emitter);
            }
        }
        userEmitters.removeAll(dead);
    }

    private void removeEmitter(Long userId, SseEmitter emitter) {
        List<SseEmitter> userEmitters = emitters.get(userId);
        if (userEmitters != null) {
            userEmitters.remove(emitter);
            if (userEmitters.isEmpty()) emitters.remove(userId);
        }
    }

    public void sendHeartbeat() {
        emitters.forEach((userId, userEmitters) -> {
            List<SseEmitter> dead = new CopyOnWriteArrayList<>();
            for (SseEmitter emitter : userEmitters) {
                try {
                    emitter.send(SseEmitter.event().comment("heartbeat"));
                } catch (Exception e) {
                    dead.add(emitter);
                }
            }
            userEmitters.removeAll(dead);
        });
    }
}
