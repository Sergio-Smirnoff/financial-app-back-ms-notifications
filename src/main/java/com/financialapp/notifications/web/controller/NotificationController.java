package com.financialapp.notifications.web.controller;

import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.pagination.PageResult;
import com.financialapp.notifications.web.controller.dto.ApiResponse;
import com.financialapp.notifications.web.controller.dto.NotificationResponse;
import com.financialapp.notifications.web.controller.dto.UnreadCountResponse;
import com.financialapp.notifications.domain.usecase.notification.AllAsReadUseCase;
import com.financialapp.notifications.domain.usecase.notification.GetLatestNotificationsByBankUseCase;
import com.financialapp.notifications.domain.usecase.notification.GetLatestNotificationsUseCase;
import com.financialapp.notifications.domain.usecase.notification.GetNotificationUseCase;
import com.financialapp.notifications.domain.usecase.notification.GetUnreadCountUseCase;
import com.financialapp.notifications.domain.usecase.notification.OneAsReadUsecase;
import com.financialapp.notifications.domain.usecase.notification.command.AllAsReadCommand;
import com.financialapp.notifications.domain.usecase.notification.command.GetLatestNotificationsByBankCommand;
import com.financialapp.notifications.domain.usecase.notification.command.GetLatestNotificationsCommand;
import com.financialapp.notifications.domain.usecase.notification.command.GetNotificationsCommand;
import com.financialapp.notifications.domain.usecase.notification.command.GetUnreadCountCommand;
import com.financialapp.notifications.domain.usecase.notification.command.MarkOneAsReadCommand;
import com.financialapp.notifications.web.controller.mapper.NotificationMapper;
import com.financialapp.notifications.web.controller.mapper.PageResultMapper;
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

    private final GetNotificationUseCase getNotificationUseCase;
    private final GetLatestNotificationsUseCase getLatestNotificationsUseCase;
    private final GetLatestNotificationsByBankUseCase getLatestNotificationsByBankUseCase;
    private final GetUnreadCountUseCase getUnreadCountUseCase;
    private final OneAsReadUsecase markAsReadUseCase;
    private final AllAsReadUseCase markAllAsReadUseCase;
    private final NotificationMapper notificationMapper;

    @GetMapping
    @Operation(summary = "Get paginated notifications")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getAll(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResult<Notification> pageResult = getNotificationUseCase.execute(new GetNotificationsCommand(userId, page, size));
        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationResponse> notifications = PageResultMapper.toPage(pageResult, pageable,
                notificationMapper::toResponse);
        return ResponseEntity.ok(ApiResponse.ok(notifications));
    }

    @GetMapping("/latest")
    @Operation(summary = "Get latest notifications, optionally filtered by bank")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getLatest(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) Long bankId) {
        List<NotificationResponse> notifications = (bankId != null
                ? getLatestNotificationsByBankUseCase.execute(new GetLatestNotificationsByBankCommand(userId, bankId))
                : getLatestNotificationsUseCase.execute(new GetLatestNotificationsCommand(userId, null)))
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(notifications));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread notification count")
    public ResponseEntity<ApiResponse<UnreadCountResponse>> getUnreadCount(
            @RequestHeader("X-User-Id") Long userId) {
        UnreadCountResponse count = UnreadCountResponse.builder()
                .count(getUnreadCountUseCase.execute(new GetUnreadCountCommand(userId)))
                .build();
        return ResponseEntity.ok(ApiResponse.ok(count));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark notification as read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        markAsReadUseCase.execute(new MarkOneAsReadCommand(userId, id));
        return ResponseEntity.ok(ApiResponse.ok("Notification marked as read", null));
    }

    @PutMapping("/read-all")
    @Operation(summary = "Mark all notifications as read")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @RequestHeader("X-User-Id") Long userId) {
        markAllAsReadUseCase.execute(new AllAsReadCommand(userId));
        return ResponseEntity.ok(ApiResponse.ok("All notifications marked as read", null));
    }
}
