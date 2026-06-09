package com.financialapp.notifications.infrastructure.kafka.event;

import com.financialapp.notifications.infrastructure.messaging.payload.BalanceAdjustedData;
import com.financialapp.notifications.infrastructure.messaging.payload.CardExpiringData;
import com.financialapp.notifications.infrastructure.messaging.payload.CardInstallmentDueData;
import com.financialapp.notifications.infrastructure.messaging.payload.InvestmentThresholdData;
import com.financialapp.notifications.infrastructure.messaging.payload.LoanReminderData;
import com.financialapp.notifications.infrastructure.messaging.payload.LowBalanceData;
import com.financialapp.notifications.infrastructure.messaging.payload.UserRegisteredData;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaEventsTest {

    @Test
    void userRegisteredData_recordEqualityAndAccessors() {
        UserRegisteredData a = new UserRegisteredData(1L, "e@x.com", "Ada", "L");
        UserRegisteredData b = new UserRegisteredData(1L, "e@x.com", "Ada", "L");
        assertThat(a.userId()).isEqualTo(1L);
        assertThat(a.email()).isEqualTo("e@x.com");
        assertThat(a.firstName()).isEqualTo("Ada");
        assertThat(a.lastName()).isEqualTo("L");
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        assertThat(a.toString()).contains("UserRegisteredData");
    }

    @Test
    void lowBalanceData_recordEqualityAndAccessors() {
        LowBalanceData a = new LowBalanceData(1L, "Savings", "001", "BANCO", new BigDecimal("50.00"), "ARS");
        assertThat(a.userId()).isEqualTo(1L);
        assertThat(a.accountName()).isEqualTo("Savings");
        assertThat(a.accountCbu()).isEqualTo("001");
        assertThat(a.bankNumber()).isEqualTo("BANCO");
        assertThat(a.balance()).isEqualByComparingTo("50.00");
        assertThat(a.currency()).isEqualTo("ARS");
        assertThat(a).isEqualTo(new LowBalanceData(1L, "Savings", "001", "BANCO", new BigDecimal("50.00"), "ARS"));
    }

    @Test
    void balanceAdjustedData_recordEqualityAndAccessors() {
        BalanceAdjustedData a = new BalanceAdjustedData(1L, "Checking", "002", "BANCO", new BigDecimal("100"), "ARS", true);
        assertThat(a.userId()).isEqualTo(1L);
        assertThat(a.accountName()).isEqualTo("Checking");
        assertThat(a.amount()).isEqualByComparingTo("100");
        assertThat(a.currency()).isEqualTo("ARS");
        assertThat(a.credit()).isTrue();
    }

    @Test
    void loanReminderData_recordEqualityAndAccessors() {
        LocalDate due = LocalDate.of(2026, 7, 1);
        LoanReminderData a = new LoanReminderData(1L, 2L, 3L, 4, "Car Loan", due);
        assertThat(a.userId()).isEqualTo(1L);
        assertThat(a.loanId()).isEqualTo(2L);
        assertThat(a.installmentId()).isEqualTo(3L);
        assertThat(a.installmentNumber()).isEqualTo(4);
        assertThat(a.loanName()).isEqualTo("Car Loan");
        assertThat(a.dueDate()).isEqualTo(due);
    }

    @Test
    void cardExpiringData_recordEqualityAndAccessors() {
        CardExpiringData a = new CardExpiringData(1L, "4111111111111234", "BANK01", "2027-12");
        assertThat(a.userId()).isEqualTo(1L);
        assertThat(a.cardNumber()).isEqualTo("4111111111111234");
        assertThat(a.bankNumber()).isEqualTo("BANK01");
        assertThat(a.expiringDate()).isEqualTo("2027-12");
    }

    @Test
    void cardInstallmentDueData_recordEqualityAndAccessors() {
        LocalDate due = LocalDate.of(2026, 8, 15);
        CardInstallmentDueData a = new CardInstallmentDueData(1L, "4111111111111234", 5L, 2, 12,
                "TV Purchase", due, new BigDecimal("150.00"), "ARS");
        assertThat(a.userId()).isEqualTo(1L);
        assertThat(a.cardNumber()).isEqualTo("4111111111111234");
        assertThat(a.installmentId()).isEqualTo(5L);
        assertThat(a.installmentNumber()).isEqualTo(2);
        assertThat(a.totalInstallments()).isEqualTo(12);
        assertThat(a.description()).isEqualTo("TV Purchase");
        assertThat(a.dueDate()).isEqualTo(due);
        assertThat(a.amount()).isEqualByComparingTo("150.00");
        assertThat(a.currency()).isEqualTo("ARS");
    }

    @Test
    void investmentThresholdData_recordEqualityAndAccessors() {
        InvestmentThresholdData a = new InvestmentThresholdData(1L, 2L, "AL30", "Bond", "GAIN",
                new BigDecimal("5"), new BigDecimal("7"), new BigDecimal("100"),
                new BigDecimal("90"), "USD");
        assertThat(a.userId()).isEqualTo(1L);
        assertThat(a.holdingId()).isEqualTo(2L);
        assertThat(a.ticker()).isEqualTo("AL30");
        assertThat(a.name()).isEqualTo("Bond");
        assertThat(a.direction()).isEqualTo("GAIN");
        assertThat(a.thresholdPct()).isEqualByComparingTo("5");
        assertThat(a.actualPct()).isEqualByComparingTo("7");
        assertThat(a.currentPrice()).isEqualByComparingTo("100");
        assertThat(a.avgPurchasePrice()).isEqualByComparingTo("90");
        assertThat(a.currency()).isEqualTo("USD");
    }
}
