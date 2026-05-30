package com.financialapp.notifications.web.controller;

import com.financialapp.notifications.infrastructure.sse.SseInAppNotificationSender;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications SSE", description = "Server-Sent Events for real-time notifications")
public class NotificationStreamController {

    private final SseInAppNotificationSender sseInAppNotificationSender;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "SSE stream for real-time notifications")
    public SseEmitter stream(@RequestHeader("X-User-Id") Long userId) {
        return sseInAppNotificationSender.createEmitter(userId);
    }
}
