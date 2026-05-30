package com.financialapp.notifications.infrastructure.scheduler;

import com.financialapp.notifications.domain.interfaces.usecase.SendMonthlySummariesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MonthlySummeryScheduler {

    private final SendMonthlySummariesUseCase useCase;

    @Scheduled(cron = "${notification.scheduler.cron:0 0 9 1 * *}")
    public void sendMonthlySummaries() {
        useCase.execute();
    }
}