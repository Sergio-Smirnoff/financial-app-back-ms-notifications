package com.financialapp.notifications.kafka.listener;

import com.financialapp.notifications.kafka.event.InvestmentThresholdEvent;
import com.financialapp.notifications.model.enums.NotificationChannel;
import com.financialapp.notifications.model.enums.NotificationType;
import com.financialapp.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InvestmentEventListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "investment.threshold.reached", groupId = "notifications-group")
    public void handleThresholdReached(InvestmentThresholdEvent event) {
        log.info("Received investment.threshold.reached event for userId={}", event.getUserId());
        InvestmentThresholdEvent.Payload p = event.getPayload();
        boolean isGain = "GAIN".equals(p.getDirection());

        String title = String.format("Investment Alert: %s %s %.2f%%",
                p.getTicker(), isGain ? "gained" : "lost", p.getActualPct().abs());
        String message = String.format(
                "Your holding %s (%s) has %s %.2f%%, crossing your %s threshold of %.2f%%. "
                        + "Current price: %s %s, avg cost: %s %s.",
                p.getName(), p.getTicker(),
                isGain ? "gained" : "lost", p.getActualPct().abs(),
                isGain ? "gain" : "loss", p.getThresholdPct(),
                p.getCurrentPrice(), p.getCurrency(),
                p.getAvgPurchasePrice(), p.getCurrency());

        notificationService.createAndDispatch(
                event.getUserId(), NotificationType.INVESTMENT_THRESHOLD, title, message,
                NotificationChannel.BOTH, null);
    }
}
