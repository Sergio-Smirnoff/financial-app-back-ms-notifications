package com.financialapp.notifications.web.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryPreferenceResponse {
    private String category;
    private boolean inAppEnabled;
    private boolean emailEnabled;
    private boolean hasUiToggle;
}
