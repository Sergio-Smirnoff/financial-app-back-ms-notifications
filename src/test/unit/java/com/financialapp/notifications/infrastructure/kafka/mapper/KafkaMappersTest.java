package com.financialapp.notifications.infrastructure.kafka.mapper;

import com.financialapp.notifications.domain.event.BalanceAdjusted;
import com.financialapp.notifications.domain.event.CardExpiring;
import com.financialapp.notifications.domain.event.InvestmentThreshold;
import com.financialapp.notifications.domain.event.LoanReminder;
import com.financialapp.notifications.domain.event.LowBalance;
import com.financialapp.notifications.domain.event.PaymentDue;
import com.financialapp.notifications.domain.event.UserRegistered;
import com.financialapp.notifications.infrastructure.messaging.mapper.BalanceAdjustedMapper;
import com.financialapp.notifications.infrastructure.messaging.mapper.CardExpiringMapper;
import com.financialapp.notifications.infrastructure.messaging.mapper.InvestmentThresholdMapper;
import com.financialapp.notifications.infrastructure.messaging.mapper.LoanReminderMapper;
import com.financialapp.notifications.infrastructure.messaging.mapper.LowBalanceMapper;
import com.financialapp.notifications.infrastructure.messaging.mapper.PaymentDueMapper;
import com.financialapp.notifications.infrastructure.messaging.mapper.UserRegisteredMapper;
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

class KafkaMappersTest {

    @Test
    void mapperUtilities_areInstantiable() {
        assertThat(new BalanceAdjustedMapper()).isNotNull();
        assertThat(new CardExpiringMapper()).isNotNull();
        assertThat(new InvestmentThresholdMapper()).isNotNull();
        assertThat(new LoanReminderMapper()).isNotNull();
        assertThat(new LowBalanceMapper()).isNotNull();
        assertThat(new PaymentDueMapper()).isNotNull();
        assertThat(new UserRegisteredMapper()).isNotNull();
    }

    @Test
    void userRegisteredMapper_copiesFields() {
        UserRegistered domain = UserRegisteredMapper.toDomain(
                new UserRegisteredData(1L, "e@x.com", "Ada", "L"));

        assertThat(domain.userId()).isEqualTo(1L);
        assertThat(domain.email()).isEqualTo("e@x.com");
        assertThat(domain.firstName()).isEqualTo("Ada");
        assertThat(domain.lastName()).isEqualTo("L");
    }

    @Test
    void investmentThresholdMapper_copiesFields() {
        InvestmentThreshold domain = InvestmentThresholdMapper.toDomain(
                new InvestmentThresholdData(1L, 2L, "AL30", "Bond", "GAIN",
                        new BigDecimal("5"), new BigDecimal("7"),
                        new BigDecimal("100"), new BigDecimal("90"), "USD"));

        assertThat(domain.userId()).isEqualTo(1L);
        assertThat(domain.holdingId()).isEqualTo(2L);
        assertThat(domain.ticker()).isEqualTo("AL30");
        assertThat(domain.direction()).isEqualTo("GAIN");
        assertThat(domain.thresholdPct()).isEqualByComparingTo("5");
        assertThat(domain.currentPrice()).isEqualByComparingTo("100");
    }

    @Test
    void loanReminderMapper_copiesFields() {
        LocalDate due = LocalDate.of(2026, 7, 1);
        LoanReminder domain = LoanReminderMapper.toDomain(
                new LoanReminderData(1L, 2L, 3L, 4, "Car Loan", due));

        assertThat(domain.userId()).isEqualTo(1L);
        assertThat(domain.loanId()).isEqualTo(2L);
        assertThat(domain.installmentId()).isEqualTo(3L);
        assertThat(domain.installmentNumber()).isEqualTo(4);
        assertThat(domain.loanName()).isEqualTo("Car Loan");
        assertThat(domain.dueDate()).isEqualTo(due);
    }

    @Test
    void paymentDueMapper_copiesFields() {
        LocalDate due = LocalDate.of(2026, 8, 15);
        PaymentDue domain = PaymentDueMapper.toDomain(
                new CardInstallmentDueData(1L, "4111111111111234", 5L, 2, 12,
                        "TV Purchase", due, new BigDecimal("150.00"), "ARS"));

        assertThat(domain.userId()).isEqualTo(1L);
        assertThat(domain.cardNumber()).isEqualTo("4111111111111234");
        assertThat(domain.installmentNumber()).isEqualTo(2);
        assertThat(domain.totalInstallments()).isEqualTo(12);
        assertThat(domain.description()).isEqualTo("TV Purchase");
        assertThat(domain.dueDate()).isEqualTo(due);
        assertThat(domain.amount()).isEqualByComparingTo("150.00");
    }

    @Test
    void lowBalanceMapper_copiesFields() {
        LowBalance domain = LowBalanceMapper.toDomain(
                new LowBalanceData(1L, "Savings", "001", "BANCO", new BigDecimal("50"), "ARS"));

        assertThat(domain.userId()).isEqualTo(1L);
        assertThat(domain.accountName()).isEqualTo("Savings");
        assertThat(domain.accountCbu()).isEqualTo("001");
        assertThat(domain.bankNumber()).isEqualTo("BANCO");
        assertThat(domain.balance()).isEqualByComparingTo("50");
        assertThat(domain.currency()).isEqualTo("ARS");
    }

    @Test
    void balanceAdjustedMapper_copiesFields() {
        BalanceAdjusted domain = BalanceAdjustedMapper.toDomain(
                new BalanceAdjustedData(1L, "Checking", "002", "BANCO", new BigDecimal("200"), "USD", false));

        assertThat(domain.userId()).isEqualTo(1L);
        assertThat(domain.accountName()).isEqualTo("Checking");
        assertThat(domain.amount()).isEqualByComparingTo("200");
        assertThat(domain.currency()).isEqualTo("USD");
        assertThat(domain.credit()).isFalse();
    }

    @Test
    void cardExpiringMapper_copiesFields() {
        CardExpiring domain = CardExpiringMapper.toDomain(
                new CardExpiringData(1L, "4111111111111234", "BANK01", "2027-12"));

        assertThat(domain.userId()).isEqualTo(1L);
        assertThat(domain.cardNumber()).isEqualTo("4111111111111234");
        assertThat(domain.bankNumber()).isEqualTo("BANK01");
        assertThat(domain.expiringDate()).isEqualTo("2027-12");
    }
}
