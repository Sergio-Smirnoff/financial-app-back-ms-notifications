package com.financialapp.notifications.web.controller;

import com.financialapp.commons.core.response.ApiResponse;
import com.financialapp.commons.web.openapi.ApiErrorCodes;
import com.financialapp.notifications.domain.exception.DomainError;
import com.financialapp.notifications.domain.model.notification.NotificationPreference;
import com.financialapp.notifications.domain.model.notification.UserNotificationPreference;
import com.financialapp.notifications.domain.usecase.preference.GetPreferenceUseCase;
import com.financialapp.notifications.domain.usecase.preference.GetPreferencesByCategoryUseCase;
import com.financialapp.notifications.domain.usecase.preference.UpdateCategoryPreferenceUseCase;
import com.financialapp.notifications.domain.usecase.preference.UpdatePreferenceUseCase;
import com.financialapp.notifications.domain.usecase.preference.command.GetPreferenceCommand;
import com.financialapp.notifications.domain.usecase.preference.command.GetPreferencesByCategoryCommand;
import com.financialapp.notifications.domain.usecase.preference.command.UpdateCategoryPreferenceCommand;
import com.financialapp.notifications.domain.usecase.preference.command.UpdatePreferenceCommand;
import com.financialapp.notifications.web.controller.dto.NotificationPreferenceResponse;
import com.financialapp.notifications.web.controller.dto.response.CategoryPreferenceResponse;
import com.financialapp.notifications.web.controller.request.NotificationPreferenceRequest;
import com.financialapp.notifications.web.controller.request.UpdateCategoryPreferenceRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications/preferences")
@RequiredArgsConstructor
@Tag(name = "Notification Preferences", description = "User notification preferences management")
public class PreferenceController {

    private final GetPreferenceUseCase getPreferenceUseCase;
    private final UpdatePreferenceUseCase updatePreferenceUseCase;
    private final GetPreferencesByCategoryUseCase getPreferencesByCategoryUseCase;
    private final UpdateCategoryPreferenceUseCase updateCategoryPreferenceUseCase;

    @GetMapping
    @Operation(summary = "Get user notification preferences")
    @ApiErrorCodes(catalog = DomainError.class, value = {"user_not_found"})
    public ResponseEntity<ApiResponse<NotificationPreferenceResponse>> getPreference(
            @RequestHeader("X-User-Id") Long userId) {
        NotificationPreferenceResponse pref = toResponse(getPreferenceUseCase.execute(new GetPreferenceCommand(userId)));
        return ResponseEntity.ok(ApiResponse.ok(pref));
    }

    @PutMapping
    @Operation(summary = "Update notification preferences")
    @ApiErrorCodes(catalog = DomainError.class, value = {"user_not_found", "business_rule_violation"})
    public ResponseEntity<ApiResponse<NotificationPreferenceResponse>> updatePreference(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody NotificationPreferenceRequest request) {
        NotificationPreferenceResponse updated = toResponse(updatePreferenceUseCase.execute(new UpdatePreferenceCommand(userId,
                request.getMonthlyEmailEnabled())));
        return ResponseEntity.ok(ApiResponse.ok("Preferences updated", updated));
    }

    @GetMapping("/by-category")
    @Operation(summary = "Get notification preferences grouped by category")
    @ApiErrorCodes(catalog = DomainError.class, value = {"user_not_found"})
    public ResponseEntity<ApiResponse<List<CategoryPreferenceResponse>>> getPreferencesByCategory(
            @RequestHeader("X-User-Id") Long userId) {
        List<NotificationPreference> preferences = getPreferencesByCategoryUseCase.execute(new GetPreferencesByCategoryCommand(userId));
        List<CategoryPreferenceResponse> responses = preferences.stream()
                .map(this::toCategoryResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(responses));
    }

    @PutMapping("/{category}")
    @Operation(summary = "Update notification preference for a specific category")
    @ApiErrorCodes(catalog = DomainError.class, value = {"business_rule_violation"})
    public ResponseEntity<ApiResponse<CategoryPreferenceResponse>> updateCategoryPreference(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("category") String category,
            @Valid @RequestBody UpdateCategoryPreferenceRequest request) {
        NotificationPreference updated = updateCategoryPreferenceUseCase.execute(
                new UpdateCategoryPreferenceCommand(userId, category, request.getInAppEnabled(), request.getEmailEnabled())
        );
        return ResponseEntity.ok(ApiResponse.ok("Category preference updated", toCategoryResponse(updated)));
    }

    private NotificationPreferenceResponse toResponse(UserNotificationPreference preference) {
        return NotificationPreferenceResponse.builder()
                .userId(preference.userId())
                .email(preference.email())
                .monthlyEmailEnabled(preference.monthlyEmailEnabled())
                .build();
    }

    private CategoryPreferenceResponse toCategoryResponse(NotificationPreference preference) {
        return CategoryPreferenceResponse.builder()
                .category(preference.category().name())
                .inAppEnabled(preference.inAppEnabled())
                .emailEnabled(preference.emailEnabled())
                .hasUiToggle(preference.category().hasUiToggle())
                .build();
    }
}
