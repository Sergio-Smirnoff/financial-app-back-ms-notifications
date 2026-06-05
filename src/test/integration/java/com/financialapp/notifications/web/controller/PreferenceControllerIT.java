package com.financialapp.notifications.web.controller;

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

    private static final String TOKEN = "test-token";

    @Test
    void getPreference_returnsDefaultWhenAbsent() throws Exception {
        // Given no stored preference for the user / When fetching it
        mockMvc.perform(get("/api/v1/notifications/preferences")
                        .header("X-User-Id", "501").header("X-Internal-Token", TOKEN))
                // Then a default enabled preference is returned
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(501))
                .andExpect(jsonPath("$.data.monthlyEmailEnabled").value(true));
    }

    @Test
    void updatePreference_persistsAndIsReadBack() throws Exception {
        // Given a disable request / When updating the preference
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
