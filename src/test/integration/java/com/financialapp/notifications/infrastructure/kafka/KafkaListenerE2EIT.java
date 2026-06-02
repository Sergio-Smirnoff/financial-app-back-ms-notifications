package com.financialapp.notifications.infrastructure.kafka;

import com.financialapp.notifications.domain.repository.NotificationRepository;
import com.financialapp.notifications.infrastructure.messaging.payload.PaymentDueEvent;
import com.financialapp.notifications.infrastructure.messaging.payload.UserRegisteredEvent;
import com.financialapp.notifications.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end Kafka IT: publishes events to the embedded broker and verifies the real
 * {@code @KafkaListener} consumers process them into persisted notifications.
 */
class KafkaListenerE2EIT extends IntegrationTestBase {

    @Autowired KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired NotificationRepository notificationRepository;

    private boolean awaitTrue(BooleanSupplier condition) throws InterruptedException {
        long deadline = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(20);
        while (System.currentTimeMillis() < deadline) {
            if (condition.getAsBoolean()) {
                return true;
            }
            Thread.sleep(200);
        }
        return condition.getAsBoolean();
    }

    @Test
    void userRegisteredEvent_isConsumed_andWelcomeNotificationPersisted() throws InterruptedException {
        // Given a user-registered event published to its topic
        kafkaTemplate.send("user.registered", UserRegisteredEvent.builder().userId(701L)
                .payload(UserRegisteredEvent.Payload.builder().email("u701@x.com")
                        .firstName("Ada").lastName("L").build()).build());

        // When the listener consumes it / Then a welcome notification is persisted for the user
        boolean persisted = awaitTrue(() ->
                notificationRepository.countByUserIdAndReadFalse(701L) >= 1);
        assertThat(persisted).isTrue();
    }

    @Test
    void paymentDueEvent_isConsumed_andNotificationPersisted() throws InterruptedException {
        // Given a payment-due event published to its topic
        kafkaTemplate.send("payment.due", PaymentDueEvent.builder().userId(702L)
                .payload(PaymentDueEvent.Payload.builder().cardExpenseId(1L).description("Visa")
                        .nextDueDate(LocalDate.of(2026, 7, 1)).installmentAmount(new BigDecimal("10"))
                        .currency("ARS").remainingInstallments(1).build()).build());

        // When the listener consumes it / Then a payment-due notification is persisted
        boolean persisted = awaitTrue(() ->
                notificationRepository.countByUserIdAndReadFalse(702L) >= 1);
        assertThat(persisted).isTrue();
    }
}
