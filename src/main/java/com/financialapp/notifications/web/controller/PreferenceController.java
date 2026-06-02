package com.financialapp.notifications.web.controller;

import com.financialapp.notifications.domain.model.notification.UserNotificationPreference;
import com.financialapp.notifications.domain.usecase.GetPreferenceUseCase;
import com.financialapp.notifications.domain.usecase.UpdatePreferenceUseCase;
import com.financialapp.notifications.web.controller.dto.ApiResponse;
import com.financialapp.notifications.web.controller.dto.NotificationPreferenceResponse;
import com.financialapp.notifications.web.controller.request.NotificationPreferenceRequest;
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

    private final GetPreferenceUseCase getPreferenceUseCase;
    private final UpdatePreferenceUseCase updatePreferenceUseCase;

    @GetMapping
    @Operation(summary = "Get user notification preferences")
    public ResponseEntity<ApiResponse<NotificationPreferenceResponse>> getPreference(
            @RequestHeader("X-User-Id") Long userId) {
        NotificationPreferenceResponse pref = toResponse(getPreferenceUseCase.execute(userId));
        return ResponseEntity.ok(ApiResponse.ok(pref));
    }

    @PutMapping
    @Operation(summary = "Update notification preferences")
    public ResponseEntity<ApiResponse<NotificationPreferenceResponse>> updatePreference(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody NotificationPreferenceRequest request) {
        NotificationPreferenceResponse updated = toResponse(updatePreferenceUseCase.execute(userId,
                request.getMonthlyEmailEnabled()));
        return ResponseEntity.ok(ApiResponse.ok("Preferences updated", updated));
    }

    private NotificationPreferenceResponse toResponse(UserNotificationPreference preference) {
        return NotificationPreferenceResponse.builder()
                .userId(preference.userId())
                .email(preference.email())
                .monthlyEmailEnabled(preference.monthlyEmailEnabled())
                .build();
    }
}
