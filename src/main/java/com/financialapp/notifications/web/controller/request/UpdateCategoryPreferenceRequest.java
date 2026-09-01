package com.financialapp.notifications.web.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCategoryPreferenceRequest {
    @NotNull
    private Boolean inAppEnabled;

    @NotNull
    private Boolean emailEnabled;
}
