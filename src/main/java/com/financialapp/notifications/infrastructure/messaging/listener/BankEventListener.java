package com.financialapp.notifications.infrastructure.messaging.listener;

import com.financialapp.notifications.domain.usecase.event.ProcessBalanceAdjustedUseCase;
import com.financialapp.notifications.domain.usecase.event.ProcessCardExpiringUseCase;
import com.financialapp.notifications.domain.usecase.event.ProcessLoanReminderUseCase;
import com.financialapp.notifications.domain.usecase.event.ProcessLowBalanceUseCase;
import com.financialapp.notifications.domain.usecase.event.ProcessPaymentDueUseCase;
import com.financialapp.notifications.domain.usecase.event.command.ProcessBalanceAdjustedCommand;
import com.financialapp.notifications.domain.usecase.event.command.ProcessCardExpiringCommand;
import com.financialapp.notifications.domain.usecase.event.command.ProcessLoanReminderCommand;
import com.financialapp.notifications.domain.usecase.event.command.ProcessLowBalanceCommand;
import com.financialapp.notifications.domain.usecase.event.command.ProcessPaymentDueCommand;
import com.financialapp.notifications.infrastructure.messaging.mapper.BalanceAdjustedMapper;
import com.financialapp.notifications.infrastructure.messaging.mapper.CardExpiringMapper;
import com.financialapp.notifications.infrastructure.messaging.mapper.LoanReminderMapper;
import com.financialapp.notifications.infrastructure.messaging.mapper.LowBalanceMapper;
import com.financialapp.notifications.infrastructure.messaging.mapper.PaymentDueMapper;
import com.financialapp.notifications.infrastructure.messaging.payload.BalanceAdjustedData;
import com.financialapp.notifications.infrastructure.messaging.payload.CardExpiringData;
import com.financialapp.notifications.infrastructure.messaging.payload.CardInstallmentDueData;
import com.financialapp.notifications.infrastructure.messaging.payload.LoanReminderData;
import com.financialapp.notifications.infrastructure.messaging.payload.LowBalanceData;
import com.financialapp.commons.messaging.infrastructure.messaging.consume.IdempotentEventProcessor;
import io.cloudevents.CloudEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BankEventListener {

    private final ProcessLowBalanceUseCase lowBalanceUseCase;
    private final ProcessBalanceAdjustedUseCase balanceAdjustedUseCase;
    private final ProcessLoanReminderUseCase loanReminderUseCase;
    private final ProcessCardExpiringUseCase cardExpiringUseCase;
    private final ProcessPaymentDueUseCase paymentDueUseCase;
    private final IdempotentEventProcessor processor;

    @KafkaListener(topics = "banks.account.low_balance", groupId = "notifications-group")
    public void handleLowBalance(CloudEvent event) {
        log.info("Received banks.account.low_balance event id={}", event.getId());
        processor.process(event, LowBalanceData.class,
                data -> lowBalanceUseCase.execute(new ProcessLowBalanceCommand(LowBalanceMapper.toDomain(data))));
    }

    @KafkaListener(topics = "banks.account.balance_adjusted", groupId = "notifications-group")
    public void handleBalanceAdjusted(CloudEvent event) {
        log.info("Received banks.account.balance_adjusted event id={}", event.getId());
        processor.process(event, BalanceAdjustedData.class,
                data -> balanceAdjustedUseCase.execute(new ProcessBalanceAdjustedCommand(BalanceAdjustedMapper.toDomain(data))));
    }

    @KafkaListener(topics = "banks.loan.reminder", groupId = "notifications-group")
    public void handleLoanReminder(CloudEvent event) {
        log.info("Received banks.loan.reminder event id={}", event.getId());
        processor.process(event, LoanReminderData.class,
                data -> loanReminderUseCase.execute(new ProcessLoanReminderCommand(LoanReminderMapper.toDomain(data))));
    }

    @KafkaListener(topics = "banks.card.expiring", groupId = "notifications-group")
    public void handleCardExpiring(CloudEvent event) {
        log.info("Received banks.card.expiring event id={}", event.getId());
        processor.process(event, CardExpiringData.class,
                data -> cardExpiringUseCase.execute(new ProcessCardExpiringCommand(CardExpiringMapper.toDomain(data))));
    }

    @KafkaListener(topics = "banks.card.installment_due", groupId = "notifications-group")
    public void handleCardInstallmentDue(CloudEvent event) {
        log.info("Received banks.card.installment_due event id={}", event.getId());
        processor.process(event, CardInstallmentDueData.class,
                data -> paymentDueUseCase.execute(new ProcessPaymentDueCommand(PaymentDueMapper.toDomain(data))));
    }
}
