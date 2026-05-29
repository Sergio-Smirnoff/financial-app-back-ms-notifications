package com.financialapp.notifications.web.controller;

import com.financialapp.notifications.domain.model.response.ApiResponse;
import com.financialapp.notifications.domain.model.response.NotificationResponse;
import com.financialapp.notifications.domain.model.response.UnreadCountResponse;
import com.financialapp.notifications.application.usecase.notification.AllAsReadUseCase;
import com.financialapp.notifications.application.usecase.notification.GetLatestNotificationsUseCase;
import com.financialapp.notifications.application.usecase.notification.GetNotificationUseCase;
import com.financialapp.notifications.application.usecase.notification.GetUnreadCountUseCase;
import com.financialapp.notifications.application.usecase.notification.OneAsReadUseCase;
import com.financialapp.notifications.application.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Notification management")
public class NotificationController {

    private final NotificationService notificationService;
    private final GetNotificationUseCase getNotificationUseCase;
    private final GetLatestNotificationsUseCase getLatestNotificationsUseCase;
    private final GetUnreadCountUseCase getUnreadCountUseCase;
    private final OneAsReadUseCase markAsReadUseCase;
    private final AllAsReadUseCase markAllAsReadUseCase;

    @GetMapping
    @Operation(summary = "Get paginated notifications")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getAll(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationResponse> notifications = getNotificationUseCase.execute(userId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(notifications));
    }

    @GetMapping("/latest")
    @Operation(summary = "Get latest notifications, optionally filtered by bank")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getLatest(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) Long bankId) {
        List<NotificationResponse> notifications = getLatestNotificationsUseCase.execute(userId, bankId);
        return ResponseEntity.ok(ApiResponse.ok(notifications));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread notification count")
    public ResponseEntity<ApiResponse<UnreadCountResponse>> getUnreadCount(
            @RequestHeader("X-User-Id") Long userId) {
        UnreadCountResponse count = getUnreadCountUseCase.execute(userId);
        return ResponseEntity.ok(ApiResponse.ok(count));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark notification as read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        markAsReadUseCase.execute(userId, id);
        return ResponseEntity.ok(ApiResponse.ok("Notification marked as read", null));
    }

    @PutMapping("/read-all")
    @Operation(summary = "Mark all notifications as read")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @RequestHeader("X-User-Id") Long userId) {
        markAllAsReadUseCase.execute(userId);
        return ResponseEntity.ok(ApiResponse.ok("All notifications marked as read", null));
    }
}
