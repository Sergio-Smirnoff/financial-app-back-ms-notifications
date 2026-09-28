package com.financialapp.notifications.application.usecase.scheduler.impl;

import com.financialapp.notifications.application.service.MonthlySummaryDelivery;
import com.financialapp.notifications.domain.model.notification.UserNotificationPreference;
import com.financialapp.notifications.domain.model.pagination.PageResult;
import com.financialapp.notifications.domain.repository.UserNotificationPreferenceRepository;
import com.financialapp.notifications.domain.usecase.notification.SendMonthlySummariesUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.YearMonth;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendMonthlySummariesUseCaseImpl implements SendMonthlySummariesUseCase {

    private static final int PAGE_SIZE = 500;

    private final UserNotificationPreferenceRepository recipients;
    private final MonthlySummaryDelivery delivery;
    private final Clock clock;

    @Override
    public void execute() {
        log.info("Starting monthly summary job");
        YearMonth month = YearMonth.now(clock).minusMonths(1);
        int pageNumber = 0;
        int totalProcessed = 0;
        PageResult<UserNotificationPreference> page;
        do {
            page = recipients.findAll(pageNumber, PAGE_SIZE);
            processPage(page.content(), month);
            totalProcessed += page.content().size();
            pageNumber++;
        } while (page.hasNext());
        log.info("Monthly summary job completed, processed {} users", totalProcessed);
    }

    private void processPage(List<UserNotificationPreference> page, YearMonth month) {
        List<CompletableFuture<Void>> futures = page.stream()
                .map(recipient -> CompletableFuture.runAsync(() -> processSingleUser(recipient, month)))
                .toList();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private void processSingleUser(UserNotificationPreference recipient, YearMonth month) {
        try {
            delivery.deliver(recipient, month);
        } catch (Exception e) {
            log.error("Failed to process monthly summary for userId={}: {}", recipient.userId(), e.getMessage());
        }
    }
}
