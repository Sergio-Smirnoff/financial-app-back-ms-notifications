package com.financialapp.notifications.support;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

/**
 * Shared base for full-context integration tests. Boots the application with the {@code test}
 * profile and an embedded Kafka broker (so {@code @KafkaListener} consumers start cleanly and
 * Kafka end-to-end ITs have a real broker). H2 + Flyway-off come from {@code application-test.yml}.
 */
@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = {
        "payment.due", "loan.reminder", "installment.reminder",
        "investment.threshold.reached", "user.registered", "bank-alerts"
})
public abstract class IntegrationTestBase {
}
