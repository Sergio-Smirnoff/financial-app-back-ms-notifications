package com.financialapp.notifications.infrastructure.kafka.config;

import com.financialapp.notifications.infrastructure.config.KafkaConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaConfigTest {

    @Test
    void kafkaConfig_declaresAllTopicsWithExpectedNames() {
        KafkaConfig config = new KafkaConfig();

        assertThat(config.usersUserRegisteredTopic()).extracting(NewTopic::name).isEqualTo("users.user.registered");
        assertThat(config.banksAccountLowBalanceTopic()).extracting(NewTopic::name).isEqualTo("banks.account.low_balance");
        assertThat(config.banksAccountBalanceAdjustedTopic()).extracting(NewTopic::name).isEqualTo("banks.account.balance_adjusted");
        assertThat(config.banksLoanReminderTopic()).extracting(NewTopic::name).isEqualTo("banks.loan.reminder");
        assertThat(config.banksCardExpiringTopic()).extracting(NewTopic::name).isEqualTo("banks.card.expiring");
        assertThat(config.banksCardInstallmentDueTopic()).extracting(NewTopic::name).isEqualTo("banks.card.installment_due");
        assertThat(config.investmentsThresholdBreachedTopic()).extracting(NewTopic::name).isEqualTo("investments.threshold.breached");
    }
}
