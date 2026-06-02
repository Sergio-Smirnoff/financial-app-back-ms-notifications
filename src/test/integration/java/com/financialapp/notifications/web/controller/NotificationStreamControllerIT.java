package com.financialapp.notifications.web.controller;

import com.financialapp.notifications.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

/** Opens the SSE stream and verifies the controller registers a live emitter for the user. */
@AutoConfigureMockMvc
class NotificationStreamControllerIT extends IntegrationTestBase {

    @Autowired MockMvc mockMvc;

    @Test
    void stream_opensSseEmitterForUser() throws Exception {
        // Given a user opening the stream / When the SSE endpoint is hit
        // Then the controller starts a long-lived async SSE request (emitter registered)
        mockMvc.perform(get("/api/v1/notifications/stream")
                        .header("X-User-Id", "601").header("X-Internal-Token", "test-token")
                        .accept(MediaType.TEXT_EVENT_STREAM))
                .andExpect(request().asyncStarted());
    }
}
