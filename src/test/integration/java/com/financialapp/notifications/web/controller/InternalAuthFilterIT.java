package com.financialapp.notifications.web.controller;

import com.financialapp.notifications.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Exercises the real InternalAuthFilter through the dispatcher. */
@AutoConfigureMockMvc
class InternalAuthFilterIT extends IntegrationTestBase {

    @Autowired MockMvc mockMvc;

    @Test
    void rejectsRequestWithWrongInternalToken() throws Exception {
        // Given a protected endpoint / When the X-Internal-Token does not match the configured token
        mockMvc.perform(get("/api/v1/notifications/unread-count")
                        .header("X-User-Id", "1").header("X-Internal-Token", "wrong-token"))
                // Then the filter rejects it
                .andExpect(status().isUnauthorized());
    }

    @Test
    void allowsActuatorWithoutToken() throws Exception {
        // Given an exempt actuator path / When hit without an internal token
        mockMvc.perform(get("/actuator/health"))
                // Then the filter bypasses auth and the request is served
                .andExpect(status().isOk());
    }
}
