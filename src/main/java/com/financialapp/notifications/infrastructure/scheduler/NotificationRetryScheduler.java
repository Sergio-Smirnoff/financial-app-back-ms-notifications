package com.financialapp.notifications.infrastructure.scheduler;

import com.financialapp.notifications.application.service.NotificationServiceImpl;
import com.financialapp.notifications.domain.model.notification.NotificationDelivery;
import com.financialapp.notifications.domain.repository.NotificationDeliveryRepository;
import com.financialapp.notifications.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationRetryScheduler {

    private final NotificationDeliveryRepository deliveryRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationServiceImpl notificationService;

    @Scheduled(fixedDelayString = "${notification.retry.poll-ms:60000}")
    public void retryFailedDeliveries() {
        List<NotificationDelivery> failed = deliveryRepository.findFailedReadyToRetry(LocalDateTime.now());
        if (failed.isEmpty()) {
            return;
        }
        log.info("Retrying {} failed notification deliveries", failed.size());
        for (NotificationDelivery delivery : failed) {
            notificationRepository.findById(delivery.notificationId())
                    .ifPresent(notification -> notificationService.retryDelivery(delivery, notification));
        }
    }
}
