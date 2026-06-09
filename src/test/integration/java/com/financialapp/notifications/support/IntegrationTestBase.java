package com.financialapp.notifications.support;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = {
        "users.user.registered",
        "banks.account.low_balance",
        "banks.account.balance_adjusted",
        "banks.loan.reminder",
        "banks.card.expiring",
        "banks.card.installment_due",
        "investments.threshold.breached"
})
public abstract class IntegrationTestBase {
}
