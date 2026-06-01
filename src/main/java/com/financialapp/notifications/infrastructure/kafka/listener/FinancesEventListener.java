package com.financialapp.notifications.infrastructure.kafka.listener;

import com.financialapp.notifications.domain.usecase.event.ProcessInstallmentReminderUseCase;
import com.financialapp.notifications.domain.usecase.event.ProcessLoanReminderUseCase;
import com.financialapp.notifications.domain.usecase.event.ProcessPaymentDueUseCase;
import com.financialapp.notifications.infrastructure.kafka.event.InstallmentReminderEvent;
import com.financialapp.notifications.infrastructure.kafka.event.LoanReminderEvent;
import com.financialapp.notifications.infrastructure.kafka.event.PaymentDueEvent;
import com.financialapp.notifications.infrastructure.kafka.mapper.InstallmentReminderMapper;
import com.financialapp.notifications.infrastructure.kafka.mapper.LoanReminderMapper;
import com.financialapp.notifications.infrastructure.kafka.mapper.PaymentDueMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FinancesEventListener {

    private final ProcessPaymentDueUseCase paymentDueUseCase;
    private final ProcessLoanReminderUseCase loanReminderUseCase;
    private final ProcessInstallmentReminderUseCase installmentReminderUseCase;

    @KafkaListener(topics = "payment.due", groupId = "notifications-group")
    public void handlePaymentDue(PaymentDueEvent event) {
        log.info("Received payment.due event for userId={}", event.getUserId());
        paymentDueUseCase.execute(PaymentDueMapper.toDomain(event));
    }

    @KafkaListener(topics = "loan.reminder", groupId = "notifications-group")
    public void handleLoanReminder(LoanReminderEvent event) {
        log.info("Received loan.reminder event for userId={}", event.getUserId());
        loanReminderUseCase.execute(LoanReminderMapper.toDomain(event));
    }

    @KafkaListener(topics = "installment.reminder", groupId = "notifications-group")
    public void handleInstallmentReminder(InstallmentReminderEvent event) {
        log.info("Received installment.reminder event for userId={}", event.getUserId());
        installmentReminderUseCase.execute(InstallmentReminderMapper.toDomain(event));
    }
}
