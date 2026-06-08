package com.financialapp.notifications.infrastructure.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.financialapp.commons.messaging.domain.gateway.ProcessedEventGateway;
import com.financialapp.commons.messaging.domain.model.EventId;
import com.financialapp.commons.messaging.infrastructure.messaging.consume.IdempotentEventProcessor;
import com.financialapp.commons.messaging.infrastructure.messaging.serde.CloudEventSerde;
import com.financialapp.notifications.domain.usecase.event.ProcessBalanceAdjustedUseCase;
import com.financialapp.notifications.domain.usecase.event.ProcessCardExpiringUseCase;
import com.financialapp.notifications.domain.usecase.event.ProcessInvestmentThresholdUseCase;
import com.financialapp.notifications.domain.usecase.event.ProcessLoanReminderUseCase;
import com.financialapp.notifications.domain.usecase.event.ProcessLowBalanceUseCase;
import com.financialapp.notifications.domain.usecase.event.ProcessPaymentDueUseCase;
import com.financialapp.notifications.domain.usecase.event.ProcessUserRegisteredUseCase;
import com.financialapp.notifications.domain.usecase.event.command.ProcessBalanceAdjustedCommand;
import com.financialapp.notifications.domain.usecase.event.command.ProcessCardExpiringCommand;
import com.financialapp.notifications.domain.usecase.event.command.ProcessInvestmentThresholdCommand;
import com.financialapp.notifications.domain.usecase.event.command.ProcessLoanReminderCommand;
import com.financialapp.notifications.domain.usecase.event.command.ProcessLowBalanceCommand;
import com.financialapp.notifications.domain.usecase.event.command.ProcessPaymentDueCommand;
import com.financialapp.notifications.domain.usecase.event.command.ProcessUserRegisteredCommand;
import com.financialapp.notifications.infrastructure.messaging.listener.BankEventListener;
import com.financialapp.notifications.infrastructure.messaging.listener.InvestmentEventListener;
import com.financialapp.notifications.infrastructure.messaging.listener.UserEventListener;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventListenersTest {

    @Mock ProcessUserRegisteredUseCase userUseCase;
    @Mock ProcessInvestmentThresholdUseCase investmentUseCase;
    @Mock ProcessLowBalanceUseCase lowBalanceUseCase;
    @Mock ProcessBalanceAdjustedUseCase balanceAdjustedUseCase;
    @Mock ProcessLoanReminderUseCase loanReminderUseCase;
    @Mock ProcessCardExpiringUseCase cardExpiringUseCase;
    @Mock ProcessPaymentDueUseCase paymentDueUseCase;
    @Mock ProcessedEventGateway processedEventGateway;

    private IdempotentEventProcessor processor;

    @BeforeEach
    void setUp() {
        when(processedEventGateway.isProcessed(any(EventId.class))).thenReturn(false);
        processor = new IdempotentEventProcessor(processedEventGateway,
                new CloudEventSerde(new ObjectMapper().registerModule(new JavaTimeModule())));
    }

    private CloudEvent buildEvent(String id, String type, String json) {
        return CloudEventBuilder.v1()
                .withId(id)
                .withSource(URI.create("/test"))
                .withType(type)
                .withData("application/json", json.getBytes(StandardCharsets.UTF_8))
                .build();
    }

    @Test
    void userEventListener_routesCloudEventToUseCase() {
        CloudEvent event = buildEvent("e-user-1", "users.user.registered",
                "{\"userId\":1,\"email\":\"a@b.com\",\"firstName\":\"Ada\",\"lastName\":\"L\"}");

        new UserEventListener(userUseCase, processor).handleUserRegistered(event);

        ArgumentCaptor<ProcessUserRegisteredCommand> captor = ArgumentCaptor.forClass(ProcessUserRegisteredCommand.class);
        verify(userUseCase).execute(captor.capture());
        assertThat(captor.getValue().user().email()).isEqualTo("a@b.com");
        assertThat(captor.getValue().user().firstName()).isEqualTo("Ada");
    }

    @Test
    void investmentEventListener_routesCloudEventToUseCase() {
        CloudEvent event = buildEvent("e-inv-1", "investments.threshold.breached",
                "{\"userId\":1,\"holdingId\":2,\"ticker\":\"AL30\",\"name\":\"Bond\",\"direction\":\"GAIN\"," +
                "\"thresholdPct\":5,\"actualPct\":7,\"currentPrice\":100,\"avgPurchasePrice\":90,\"currency\":\"USD\"}");

        new InvestmentEventListener(investmentUseCase, processor).handleThresholdBreached(event);

        ArgumentCaptor<ProcessInvestmentThresholdCommand> captor = ArgumentCaptor.forClass(ProcessInvestmentThresholdCommand.class);
        verify(investmentUseCase).execute(captor.capture());
        assertThat(captor.getValue().threshold().ticker()).isEqualTo("AL30");
    }

    @Test
    void bankEventListener_routesLowBalanceToUseCase() {
        CloudEvent event = buildEvent("e-lb-1", "banks.account.low_balance",
                "{\"userId\":1,\"accountName\":\"Savings\",\"accountCbu\":\"001\",\"bankNumber\":\"BANCO\",\"balance\":50,\"currency\":\"ARS\"}");

        BankEventListener listener = new BankEventListener(
                lowBalanceUseCase, balanceAdjustedUseCase, loanReminderUseCase,
                cardExpiringUseCase, paymentDueUseCase, processor);
        listener.handleLowBalance(event);

        ArgumentCaptor<ProcessLowBalanceCommand> captor = ArgumentCaptor.forClass(ProcessLowBalanceCommand.class);
        verify(lowBalanceUseCase).execute(captor.capture());
        assertThat(captor.getValue().lowBalance().accountName()).isEqualTo("Savings");
    }

    @Test
    void bankEventListener_routesLoanReminderToUseCase() {
        LocalDate due = LocalDate.of(2026, 8, 1);
        CloudEvent event = buildEvent("e-lr-1", "banks.loan.reminder",
                "{\"userId\":1,\"loanId\":2,\"installmentId\":3,\"installmentNumber\":4,\"loanName\":\"Car Loan\",\"dueDate\":\"" + due + "\"}");

        BankEventListener listener = new BankEventListener(
                lowBalanceUseCase, balanceAdjustedUseCase, loanReminderUseCase,
                cardExpiringUseCase, paymentDueUseCase, processor);
        listener.handleLoanReminder(event);

        ArgumentCaptor<ProcessLoanReminderCommand> captor = ArgumentCaptor.forClass(ProcessLoanReminderCommand.class);
        verify(loanReminderUseCase).execute(captor.capture());
        assertThat(captor.getValue().reminder().loanName()).isEqualTo("Car Loan");
    }

    @Test
    void bankEventListener_routesCardExpiringToUseCase() {
        CloudEvent event = buildEvent("e-ce-1", "banks.card.expiring",
                "{\"userId\":1,\"cardNumber\":\"4111111111111234\",\"bankNumber\":\"BANK01\",\"expiringDate\":\"2027-12\"}");

        BankEventListener listener = new BankEventListener(
                lowBalanceUseCase, balanceAdjustedUseCase, loanReminderUseCase,
                cardExpiringUseCase, paymentDueUseCase, processor);
        listener.handleCardExpiring(event);

        ArgumentCaptor<ProcessCardExpiringCommand> captor = ArgumentCaptor.forClass(ProcessCardExpiringCommand.class);
        verify(cardExpiringUseCase).execute(captor.capture());
        assertThat(captor.getValue().cardExpiring().cardNumber()).isEqualTo("4111111111111234");
    }

    @Test
    void bankEventListener_routesCardInstallmentDueToUseCase() {
        CloudEvent event = buildEvent("e-cid-1", "banks.card.installment_due",
                "{\"userId\":1,\"cardNumber\":\"4111111111111234\",\"installmentId\":5,\"installmentNumber\":2," +
                "\"totalInstallments\":12,\"description\":\"TV\",\"dueDate\":\"2026-08-15\",\"amount\":150,\"currency\":\"ARS\"}");

        BankEventListener listener = new BankEventListener(
                lowBalanceUseCase, balanceAdjustedUseCase, loanReminderUseCase,
                cardExpiringUseCase, paymentDueUseCase, processor);
        listener.handleCardInstallmentDue(event);

        ArgumentCaptor<ProcessPaymentDueCommand> captor = ArgumentCaptor.forClass(ProcessPaymentDueCommand.class);
        verify(paymentDueUseCase).execute(captor.capture());
        assertThat(captor.getValue().paymentDue().description()).isEqualTo("TV");
    }
}
