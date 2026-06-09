package com.financialapp.notifications.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic usersUserRegisteredTopic() {
        return TopicBuilder.name("users.user.registered").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic banksAccountLowBalanceTopic() {
        return TopicBuilder.name("banks.account.low_balance").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic banksAccountBalanceAdjustedTopic() {
        return TopicBuilder.name("banks.account.balance_adjusted").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic banksLoanReminderTopic() {
        return TopicBuilder.name("banks.loan.reminder").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic banksCardExpiringTopic() {
        return TopicBuilder.name("banks.card.expiring").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic banksCardInstallmentDueTopic() {
        return TopicBuilder.name("banks.card.installment_due").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic investmentsThresholdBreachedTopic() {
        return TopicBuilder.name("investments.threshold.breached").partitions(1).replicas(1).build();
    }
}
