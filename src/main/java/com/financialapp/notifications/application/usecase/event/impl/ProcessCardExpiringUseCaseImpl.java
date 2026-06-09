package com.financialapp.notifications.application.usecase.event.impl;

import com.financialapp.notifications.application.service.NotificationChannelResolver;
import com.financialapp.notifications.domain.event.CardExpiring;
import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import com.financialapp.notifications.domain.service.NotificationService;
import com.financialapp.notifications.domain.usecase.event.ProcessCardExpiringUseCase;
import com.financialapp.notifications.domain.usecase.event.command.ProcessCardExpiringCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessCardExpiringUseCaseImpl implements ProcessCardExpiringUseCase {

    private final NotificationService notificationService;
    private final NotificationChannelResolver channelResolver;

    @Override
    public void execute(ProcessCardExpiringCommand command) {
        CardExpiring ce = command.cardExpiring();
        String maskedCard = maskCardNumber(ce.cardNumber());
        String title = "Card Expiring Soon: " + maskedCard;
        String message = String.format(
                "Your card ending in %s expires on %s. Please renew it to avoid service interruptions.",
                maskedCard, ce.expiringDate());

        NotificationChannel channel = channelResolver.resolve(ce.userId());
        var newNotification = Notification.create(
                ce.userId(), NotificationType.CARD_EXPIRING, title, message, channel, null);
        notificationService.notify(newNotification);
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return cardNumber;
        }
        return "****" + cardNumber.substring(cardNumber.length() - 4);
    }
}
