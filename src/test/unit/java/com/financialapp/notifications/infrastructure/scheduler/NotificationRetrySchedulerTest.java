package com.financialapp.notifications.infrastructure.scheduler;

import com.financialapp.notifications.application.service.NotificationServiceImpl;
import com.financialapp.notifications.domain.model.notification.DeliveryStatus;
import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationDelivery;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import com.financialapp.notifications.domain.repository.NotificationDeliveryRepository;
import com.financialapp.notifications.domain.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationRetrySchedulerTest {

    @Mock NotificationDeliveryRepository deliveryRepository;
    @Mock NotificationRepository notificationRepository;
    @Mock NotificationServiceImpl notificationService;
    @InjectMocks NotificationRetryScheduler scheduler;

    private NotificationDelivery failedDelivery() {
        return new NotificationDelivery(10L, 1L, NotificationChannel.EMAIL,
                DeliveryStatus.FAILED, 1, "SMTP down",
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusHours(1), LocalDateTime.now());
    }

    private Notification notification() {
        return new Notification(1L, 9L, NotificationType.PAYMENT_DUE,
                "t", "m", NotificationChannel.EMAIL, false, null, LocalDateTime.now());
    }

    @Test
    void retryFailedDeliveries_invokesRetryForEachFailedDelivery() {
        NotificationDelivery failed = failedDelivery();
        Notification notif = notification();
        when(deliveryRepository.findFailedReadyToRetry(any())).thenReturn(List.of(failed));
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notif));

        scheduler.retryFailedDeliveries();

        ArgumentCaptor<NotificationDelivery> deliveryCaptor = ArgumentCaptor.forClass(NotificationDelivery.class);
        ArgumentCaptor<Notification> notifCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationService).retryDelivery(deliveryCaptor.capture(), notifCaptor.capture());
        assertThat(deliveryCaptor.getValue().id()).isEqualTo(10L);
        assertThat(notifCaptor.getValue().id()).isEqualTo(1L);
    }

    @Test
    void retryFailedDeliveries_doesNothingWhenNoFailedDeliveries() {
        when(deliveryRepository.findFailedReadyToRetry(any())).thenReturn(List.of());

        scheduler.retryFailedDeliveries();

        verifyNoInteractions(notificationService);
    }

    @Test
    void retryFailedDeliveries_skipsWhenNotificationNotFound() {
        NotificationDelivery failed = failedDelivery();
        when(deliveryRepository.findFailedReadyToRetry(any())).thenReturn(List.of(failed));
        when(notificationRepository.findById(1L)).thenReturn(Optional.empty());

        scheduler.retryFailedDeliveries();

        verifyNoInteractions(notificationService);
    }
}
