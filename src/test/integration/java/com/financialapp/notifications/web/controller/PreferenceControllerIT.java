package com.financialapp.notifications.web.controller;

import com.financialapp.notifications.domain.model.notification.UserNotificationPreference;
import com.financialapp.notifications.domain.repository.UserNotificationPreferenceRepository;
import com.financialapp.notifications.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Drives the PreferenceController and exercises the validation + persistence round-trip. */
@AutoConfigureMockMvc
class PreferenceControllerIT extends IntegrationTestBase {

    @Autowired MockMvc mockMvc;
    @Autowired UserNotificationPreferenceRepository preferenceRepository;

    private static final String TOKEN = "test-token";

    @Test
    void getPreference_returnsStoredPreference() throws Exception {
        // Given a stored preference for the user
        preferenceRepository.save(UserNotificationPreference.create(501L, "u501@x.com"));

        // When fetching it / Then the stored preference is returned
        mockMvc.perform(get("/api/v1/notifications/preferences")
                        .header("X-User-Id", "501").header("X-Internal-Token", TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(501))
                .andExpect(jsonPath("$.data.monthlyEmailEnabled").value(true));
    }

    @Test
    void getPreference_returnsNotFoundWhenAbsent() throws Exception {
        // Given no stored preference for the user / When fetching it
        mockMvc.perform(get("/api/v1/notifications/preferences")
                        .header("X-User-Id", "599").header("X-Internal-Token", TOKEN))
                // Then a user_not_found error is returned
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("user_not_found"));
    }

    @Test
    void updatePreference_persistsAndIsReadBack() throws Exception {
        // Given a stored preference and a disable request / When updating it
        preferenceRepository.save(UserNotificationPreference.create(502L, "u502@x.com"));

        mockMvc.perform(put("/api/v1/notifications/preferences")
                        .header("X-User-Id", "502").header("X-Internal-Token", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"monthlyEmailEnabled\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.monthlyEmailEnabled").value(false));

        // Then the update is reflected on a subsequent read
        mockMvc.perform(get("/api/v1/notifications/preferences")
                        .header("X-User-Id", "502").header("X-Internal-Token", TOKEN))
                .andExpect(jsonPath("$.data.monthlyEmailEnabled").value(false));
    }

    @Test
    void updatePreference_rejectsMissingField_withValidationError() throws Exception {
        // Given a body missing the required field / When updating
        mockMvc.perform(put("/api/v1/notifications/preferences")
                        .header("X-User-Id", "503").header("X-Internal-Token", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                // Then the validation handler returns a 400 with the field message
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("validation_error"))
                .andExpect(jsonPath("$.message").value("Request validation failed"));
    }
}
