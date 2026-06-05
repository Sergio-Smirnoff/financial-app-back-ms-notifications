package com.financialapp.notifications.web.controller;


import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import com.financialapp.notifications.domain.repository.NotificationRepository;
import com.financialapp.notifications.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Drives the NotificationController through the real use cases, MapStruct mapper and H2 persistence. */
@AutoConfigureMockMvc
class NotificationControllerIT extends IntegrationTestBase {

    @Autowired MockMvc mockMvc;
    @Autowired NotificationRepository repository;

    private static final String TOKEN = "test-token";

    private Long seed(Long userId, boolean read) {
        Notification saved = repository.save(new Notification(null, userId,
                NotificationType.PAYMENT_DUE, "Pay", "due",
                NotificationChannel.IN_APP, read, null, null));
        return saved.id();
    }

    @Test
    void getAll_returnsPagedNotifications() throws Exception {
        // Given a notification for the user
        seed(301L, false);

        // When listing the first page / Then it is returned mapped through MapStruct
        mockMvc.perform(get("/api/v1/notifications")
                        .header("X-User-Id", "301").header("X-Internal-Token", TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content[0].type").value("PAYMENT_DUE"))
                .andExpect(jsonPath("$.data.content[0].title").value("Pay"));
    }

    @Test
    void getLatest_withoutBank_returnsTop5() throws Exception {
        // Given a notification for the user
        seed(302L, false);

        // When fetching latest without a bank filter
        mockMvc.perform(get("/api/v1/notifications/latest")
                        .header("X-User-Id", "302").header("X-Internal-Token", TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].type").value("PAYMENT_DUE"));
    }

    @Test
    void getUnreadCount_reflectsStoredState() throws Exception {
        // Given one unread and one read notification
        seed(303L, false);
        seed(303L, true);

        // When fetching the unread count / Then only the unread one is counted
        mockMvc.perform(get("/api/v1/notifications/unread-count")
                        .header("X-User-Id", "303").header("X-Internal-Token", TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.count").value(1));
    }

    @Test
    void markAsRead_marksOwnedNotification() throws Exception {
        // Given an unread notification
        Long id = seed(304L, false);

        // When marking it read / Then the call succeeds and the unread count drops to zero
        mockMvc.perform(put("/api/v1/notifications/{id}/read", id)
                        .header("X-User-Id", "304").header("X-Internal-Token", TOKEN))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/notifications/unread-count")
                        .header("X-User-Id", "304").header("X-Internal-Token", TOKEN))
                .andExpect(jsonPath("$.data.count").value(0));
    }

    @Test
    void markAsRead_returns404ForUnknownNotification() throws Exception {
        // Given no such notification / When marking it read / Then a 404 is returned by the handler
        mockMvc.perform(put("/api/v1/notifications/{id}/read", 999999)
                        .header("X-User-Id", "305").header("X-Internal-Token", TOKEN))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("resource_not_found"));
    }

    @Test
    void markAllAsRead_clearsUnread() throws Exception {
        // Given two unread notifications
        seed(306L, false);
        seed(306L, false);

        // When marking all read / Then the unread count becomes zero
        mockMvc.perform(put("/api/v1/notifications/read-all")
                        .header("X-User-Id", "306").header("X-Internal-Token", TOKEN))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/notifications/unread-count")
                        .header("X-User-Id", "306").header("X-Internal-Token", TOKEN))
                .andExpect(jsonPath("$.data.count").value(0));
    }
}
