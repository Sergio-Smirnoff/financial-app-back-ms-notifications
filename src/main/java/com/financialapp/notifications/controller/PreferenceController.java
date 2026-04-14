package com.financialapp.notifications.controller;

import com.financialapp.notifications.model.dto.request.NotificationPreferenceRequest;
import com.financialapp.notifications.model.dto.response.ApiResponse;
import com.financialapp.notifications.model.dto.response.NotificationPreferenceResponse;
import com.financialapp.notifications.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications/preferences")
@RequiredArgsConstructor
@Tag(name = "Notification Preferences", description = "User notification preferences management")
public class PreferenceController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "Get user notification preferences")
    public ResponseEntity<ApiResponse<NotificationPreferenceResponse>> getPreference(
            @RequestHeader("X-User-Id") Long userId) {
        NotificationPreferenceResponse pref = notificationService.getPreference(userId);
        return ResponseEntity.ok(ApiResponse.ok(pref));
    }

    @PutMapping
    @Operation(summary = "Update notification preferences")
    public ResponseEntity<ApiResponse<NotificationPreferenceResponse>> updatePreference(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody NotificationPreferenceRequest request) {
        NotificationPreferenceResponse updated = notificationService.updatePreference(userId, request.getMonthlyEmailEnabled());
        return ResponseEntity.ok(ApiResponse.ok("Preferences updated", updated));
    }
}
