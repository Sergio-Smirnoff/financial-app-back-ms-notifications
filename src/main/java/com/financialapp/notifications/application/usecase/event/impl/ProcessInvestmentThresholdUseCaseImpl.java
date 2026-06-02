package com.financialapp.notifications.application.usecase.event.impl;

import com.financialapp.notifications.domain.service.NotificationService;
import com.financialapp.notifications.domain.usecase.event.ProcessInvestmentThresholdUseCase;
import com.financialapp.notifications.domain.usecase.event.command.ProcessInvestmentThresholdCommand;
import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.event.InvestmentThreshold;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ProcessInvestmentThresholdUseCaseImpl implements ProcessInvestmentThresholdUseCase {

    private static final Locale MESSAGE_LOCALE = Locale.of("es", "AR");

    private final NotificationService notificationService;

    @Override
    public void execute(ProcessInvestmentThresholdCommand command) {
        InvestmentThreshold t = command.threshold();
        boolean isGain = "GAIN".equals(t.direction());

        String title = String.format(MESSAGE_LOCALE, "Investment Alert: %s %s %.2f%%",
                t.ticker(), isGain ? "gained" : "lost", t.actualPct().abs());
        String message = String.format(MESSAGE_LOCALE,
                "Your holding %s (%s) has %s %.2f%%, crossing your %s threshold of %.2f%%. Current price: %s %s, avg cost: %s %s.",
                t.name(), t.ticker(),
                isGain ? "gained" : "lost", t.actualPct().abs(),
                isGain ? "gain" : "loss", t.thresholdPct(),
                t.currentPrice(), t.currency(),
                t.avgPurchasePrice(), t.currency());

        var newNotification = Notification.create(
                t.userId(), NotificationType.INVESTMENT_THRESHOLD, title, message,
                NotificationChannel.BOTH, null);
        notificationService.notify(newNotification);
    }
}
