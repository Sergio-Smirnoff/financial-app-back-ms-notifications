package com.financialapp.notifications.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.converter.ByteArrayJsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;

@Configuration
public class KafkaConfig {

    @Bean
    public RecordMessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new ByteArrayJsonMessageConverter(objectMapper);
    }

    @Bean
    public NewTopic paymentDueTopic() {
        return TopicBuilder.name("payment.due").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic loanReminderTopic() {
        return TopicBuilder.name("loan.reminder").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic installmentReminderTopic() {
        return TopicBuilder.name("installment.reminder").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic investmentThresholdTopic() {
        return TopicBuilder.name("investment.threshold.reached").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic userRegisteredTopic() {
        return TopicBuilder.name("user.registered").partitions(1).replicas(1).build();
    }
}
