package com.financialapp.notifications.infrastructure.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class KafkaConfigTest {

    @Test
    void kafkaConfig_declaresAllTopicsWithExpectedNames() {
        // Given the topic config / When the @Bean methods are invoked
        KafkaConfig config = new KafkaConfig();

        // Then each topic is named as expected
        assertThat(config.paymentDueTopic()).extracting(NewTopic::name).isEqualTo("payment.due");
        assertThat(config.loanReminderTopic()).extracting(NewTopic::name).isEqualTo("loan.reminder");
        assertThat(config.installmentReminderTopic()).extracting(NewTopic::name).isEqualTo("installment.reminder");
        assertThat(config.investmentThresholdTopic()).extracting(NewTopic::name).isEqualTo("investment.threshold.reached");
        assertThat(config.userRegisteredTopic()).extracting(NewTopic::name).isEqualTo("user.registered");
    }

    @Test
    @SuppressWarnings("unchecked")
    void errorHandlerConfig_buildsDefaultErrorHandler() {
        // Given a Kafka template / When the error-handler bean is built
        KafkaTemplate<Object, Object> template = mock(KafkaTemplate.class);
        DefaultErrorHandler handler = new KafkaErrorHandlerConfig().errorHandler(template);

        // Then a configured error handler is produced
        assertThat(handler).isNotNull();
    }
}
