package com.financialapp.notifications.application.usecase.event;

import com.financialapp.notifications.application.service.NotificationService;
import com.financialapp.notifications.domain.interfaces.usecase.event.ProcessInvestmentThresholdUseCase;
import com.financialapp.notifications.domain.model.entity.event.InvestmentThreshold;
import com.financialapp.notifications.domain.model.entity.enums.NotificationChannel;
import com.financialapp.notifications.domain.model.entity.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessInvestmentThresholdUseCaseImpl implements ProcessInvestmentThresholdUseCase {

    private final NotificationService notificationService;

    @Override
    public void execute(InvestmentThreshold t) {
        boolean isGain = "GAIN".equals(t.direction());

        String title = String.format("Investment Alert: %s %s %.2f%%",
                t.ticker(), isGain ? "gained" : "lost", t.actualPct().abs());
        String message = String.format(
                "Your holding %s (%s) has %s %.2f%%, crossing your %s threshold of %.2f%%. Current price: %s %s, avg cost: %s %s.",
                t.name(), t.ticker(),
                isGain ? "gained" : "lost", t.actualPct().abs(),
                isGain ? "gain" : "loss", t.thresholdPct(),
                t.currentPrice(), t.currency(),
                t.avgPurchasePrice(), t.currency());

        notificationService.notify(
                t.userId(), NotificationType.INVESTMENT_THRESHOLD, title, message,
                NotificationChannel.BOTH, null);
    }
}
