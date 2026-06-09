package com.financialapp.notifications.infrastructure.kafka;

import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.repository.NotificationRepository;
import com.financialapp.notifications.support.IntegrationTestBase;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class KafkaListenerE2EIT extends IntegrationTestBase {

    @Autowired KafkaTemplate<String, CloudEvent> kafkaTemplate;
    @Autowired NotificationRepository notificationRepository;

    private CloudEvent event(String type, String json) {
        return CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withSource(URI.create("/financial-app/test"))
                .withType(type)
                .withData("application/json", json.getBytes(StandardCharsets.UTF_8))
                .build();
    }

    @Test
    void userRegisteredEvent_flowsThroughBrokerAndPersistsNotification() {
        long userId = 900_001L;
        kafkaTemplate.send("users.user.registered", event("users.user.registered",
                "{\"userId\":" + userId + ",\"email\":\"e2e@x.com\",\"firstName\":\"Ada\",\"lastName\":\"L\"}"));

        await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> {
            List<Notification> notifications = notificationRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId);
            assertThat(notifications).isNotEmpty();
        });
    }

    @Test
    void lowBalanceEvent_flowsThroughBrokerAndPersistsNotification() {
        long userId = 900_002L;
        kafkaTemplate.send("banks.account.low_balance", event("banks.account.low_balance",
                "{\"userId\":" + userId + ",\"accountName\":\"Savings\",\"accountCbu\":\"001\"," +
                "\"bankNumber\":\"BANCO\",\"balance\":50,\"currency\":\"ARS\"}"));

        await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> {
            List<Notification> notifications = notificationRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId);
            assertThat(notifications).isNotEmpty();
        });
    }
}
