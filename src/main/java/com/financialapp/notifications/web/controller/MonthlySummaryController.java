package com.financialapp.notifications.web.controller;

import com.financialapp.commons.core.response.ApiResponse;
import com.financialapp.notifications.domain.usecase.notification.EnsureMonthlySummaryUseCase;
import com.financialapp.notifications.domain.usecase.notification.MonthlySummaryResult;
import com.financialapp.notifications.domain.usecase.notification.command.EnsureMonthlySummaryCommand;
import com.financialapp.notifications.web.controller.dto.response.MonthlySummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications/monthly-summary")
@RequiredArgsConstructor
@Tag(name = "Monthly Summary", description = "Idempotent monthly summary delivery")
public class MonthlySummaryController {

    private final EnsureMonthlySummaryUseCase ensureMonthlySummaryUseCase;

    @PostMapping("/ensure")
    @Operation(summary = "Send the caller's previous-month summary unless it was already sent")
    public ResponseEntity<ApiResponse<MonthlySummaryResponse>> ensure(@RequestHeader("X-User-Id") Long userId) {
        MonthlySummaryResult result = ensureMonthlySummaryUseCase.execute(new EnsureMonthlySummaryCommand(userId));
        MonthlySummaryResponse response = new MonthlySummaryResponse(
                result.sent(), result.month().toString(), result.skip() == null ? null : result.skip().code());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
