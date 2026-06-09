package com.financialapp.notifications.application.service;

import com.financialapp.notifications.domain.messaging.EmailSender;
import com.financialapp.notifications.domain.messaging.InAppNotificationSender;
import com.financialapp.notifications.domain.model.notification.DeliveryStatus;
import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationDelivery;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import com.financialapp.notifications.domain.model.notification.UserNotificationPreference;
import com.financialapp.notifications.domain.repository.NotificationDeliveryRepository;
import com.financialapp.notifications.domain.repository.NotificationRepository;
import com.financialapp.notifications.domain.repository.UserNotificationPreferenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock NotificationRepository notificationRepository;
    @Mock UserNotificationPreferenceRepository preferenceRepository;
    @Mock InAppNotificationSender inAppNotificationSender;
    @Mock EmailSender emailSender;
    @Mock NotificationDeliveryRepository deliveryRepository;

    NotificationServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new NotificationServiceImpl(
                notificationRepository, preferenceRepository,
                inAppNotificationSender, emailSender, deliveryRepository);
    }

    private Notification notification(NotificationChannel channel) {
        return new Notification(1L, 9L, NotificationType.PAYMENT_DUE, "t", "m", channel, false, null, null);
    }

    private NotificationDelivery pendingDelivery(NotificationChannel channel) {
        return new NotificationDelivery(10L, 1L, channel, DeliveryStatus.PENDING, 0, null, null,
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void notify_inAppChannel_savesAndSendsInAppOnly() {
        Notification n = notification(NotificationChannel.IN_APP);
        when(notificationRepository.save(n)).thenReturn(n);
        when(deliveryRepository.save(any())).thenReturn(pendingDelivery(NotificationChannel.IN_APP));

        service.notify(n);

        verify(notificationRepository).save(n);
        verify(inAppNotificationSender).sendToUser(9L, n);
        verifyNoInteractions(emailSender);
        verify(preferenceRepository, never()).findByUserId(any());
    }

    @Test
    void notify_emailChannel_savesAndEmailsWhenPreferenceExists() {
        Notification n = notification(NotificationChannel.EMAIL);
        when(notificationRepository.save(n)).thenReturn(n);
        when(deliveryRepository.save(any())).thenReturn(pendingDelivery(NotificationChannel.EMAIL));
        when(preferenceRepository.findByUserId(9L)).thenReturn(Optional.of(
                new UserNotificationPreference(null, 9L, "to@x.com", true, null, null)));

        service.notify(n);

        verify(emailSender).sendSimpleNotification("to@x.com", "t", "m");
        verify(inAppNotificationSender, never()).sendToUser(any(), any());
    }

    @Test
    void notify_emailChannel_skipsEmailWhenNoPreference() {
        Notification n = notification(NotificationChannel.EMAIL);
        when(notificationRepository.save(n)).thenReturn(n);
        when(preferenceRepository.findByUserId(9L)).thenReturn(Optional.empty());

        service.notify(n);

        verify(emailSender, never()).sendSimpleNotification(any(), any(), any());
    }

    @Test
    void notify_bothChannel_sendsInAppAndEmail() {
        Notification n = notification(NotificationChannel.BOTH);
        when(notificationRepository.save(n)).thenReturn(n);
        when(deliveryRepository.save(any()))
                .thenReturn(pendingDelivery(NotificationChannel.IN_APP))
                .thenReturn(pendingDelivery(NotificationChannel.EMAIL));
        when(preferenceRepository.findByUserId(9L)).thenReturn(Optional.of(
                new UserNotificationPreference(null, 9L, "to@x.com", true, null, null)));

        service.notify(n);

        verify(inAppNotificationSender).sendToUser(eq(9L), eq(n));
        verify(emailSender).sendSimpleNotification("to@x.com", "t", "m");
    }

    @Test
    void notify_inAppFailure_marksDeliveryFailed() {
        Notification n = notification(NotificationChannel.IN_APP);
        when(notificationRepository.save(n)).thenReturn(n);
        NotificationDelivery pending = pendingDelivery(NotificationChannel.IN_APP);
        when(deliveryRepository.save(any())).thenReturn(pending);
        doThrow(new RuntimeException("SSE down")).when(inAppNotificationSender).sendToUser(any(), any());

        service.notify(n);

        ArgumentCaptor<NotificationDelivery> captor = ArgumentCaptor.forClass(NotificationDelivery.class);
        verify(deliveryRepository).updateStatus(captor.capture());
        assertThat(captor.getValue().status()).isEqualTo(DeliveryStatus.FAILED);
        assertThat(captor.getValue().lastError()).contains("SSE down");
        assertThat(captor.getValue().nextRetryAt()).isNotNull();
    }

    @Test
    void notify_emailFailure_marksDeliveryFailed() {
        Notification n = notification(NotificationChannel.EMAIL);
        when(notificationRepository.save(n)).thenReturn(n);
        NotificationDelivery pending = pendingDelivery(NotificationChannel.EMAIL);
        when(deliveryRepository.save(any())).thenReturn(pending);
        when(preferenceRepository.findByUserId(9L)).thenReturn(Optional.of(
                new UserNotificationPreference(null, 9L, "to@x.com", true, null, null)));
        doThrow(new RuntimeException("SMTP down")).when(emailSender).sendSimpleNotification(any(), any(), any());

        service.notify(n);

        ArgumentCaptor<NotificationDelivery> captor = ArgumentCaptor.forClass(NotificationDelivery.class);
        verify(deliveryRepository).updateStatus(captor.capture());
        assertThat(captor.getValue().status()).isEqualTo(DeliveryStatus.FAILED);
        assertThat(captor.getValue().lastError()).contains("SMTP down");
    }

    @Test
    void retryDelivery_inApp_success_marksDeliverySent() {
        Notification n = notification(NotificationChannel.IN_APP);
        NotificationDelivery failedDelivery = pendingDelivery(NotificationChannel.IN_APP);

        service.retryDelivery(failedDelivery, n);

        verify(inAppNotificationSender).sendToUser(9L, n);
        ArgumentCaptor<NotificationDelivery> captor = ArgumentCaptor.forClass(NotificationDelivery.class);
        verify(deliveryRepository).updateStatus(captor.capture());
        assertThat(captor.getValue().status()).isEqualTo(DeliveryStatus.SENT);
        assertThat(captor.getValue().nextRetryAt()).isNull();
    }

    @Test
    void retryDelivery_inApp_senderThrows_marksDeliveryFailedWithRetry() {
        Notification n = notification(NotificationChannel.IN_APP);
        NotificationDelivery failedDelivery = pendingDelivery(NotificationChannel.IN_APP);
        doThrow(new RuntimeException("SSE down")).when(inAppNotificationSender).sendToUser(any(), any());

        service.retryDelivery(failedDelivery, n);

        ArgumentCaptor<NotificationDelivery> captor = ArgumentCaptor.forClass(NotificationDelivery.class);
        verify(deliveryRepository).updateStatus(captor.capture());
        assertThat(captor.getValue().status()).isEqualTo(DeliveryStatus.FAILED);
        assertThat(captor.getValue().lastError()).contains("SSE down");
        assertThat(captor.getValue().nextRetryAt()).isNotNull();
        assertThat(captor.getValue().attempts()).isEqualTo(1);
    }

    @Test
    void retryDelivery_email_success_marksDeliverySent() {
        Notification n = notification(NotificationChannel.EMAIL);
        NotificationDelivery failedDelivery = pendingDelivery(NotificationChannel.EMAIL);
        when(preferenceRepository.findByUserId(9L)).thenReturn(Optional.of(
                new UserNotificationPreference(null, 9L, "to@x.com", true, null, null)));

        service.retryDelivery(failedDelivery, n);

        verify(emailSender).sendSimpleNotification("to@x.com", "t", "m");
        ArgumentCaptor<NotificationDelivery> captor = ArgumentCaptor.forClass(NotificationDelivery.class);
        verify(deliveryRepository).updateStatus(captor.capture());
        assertThat(captor.getValue().status()).isEqualTo(DeliveryStatus.SENT);
    }

    @Test
    void retryDelivery_email_senderThrows_marksDeliveryFailedWithIncrementedAttempts() {
        Notification n = notification(NotificationChannel.EMAIL);
        NotificationDelivery failedDelivery = pendingDelivery(NotificationChannel.EMAIL);
        when(preferenceRepository.findByUserId(9L)).thenReturn(Optional.of(
                new UserNotificationPreference(null, 9L, "to@x.com", true, null, null)));
        doThrow(new RuntimeException("SMTP down")).when(emailSender).sendSimpleNotification(any(), any(), any());

        service.retryDelivery(failedDelivery, n);

        ArgumentCaptor<NotificationDelivery> captor = ArgumentCaptor.forClass(NotificationDelivery.class);
        verify(deliveryRepository).updateStatus(captor.capture());
        assertThat(captor.getValue().status()).isEqualTo(DeliveryStatus.FAILED);
        assertThat(captor.getValue().lastError()).contains("SMTP down");
        assertThat(captor.getValue().nextRetryAt()).isNotNull();
        assertThat(captor.getValue().attempts()).isEqualTo(1);
    }

    @Test
    void retryDelivery_email_noPreference_returnsWithoutSendingOrUpdating() {
        Notification n = notification(NotificationChannel.EMAIL);
        NotificationDelivery delivery = pendingDelivery(NotificationChannel.EMAIL);
        when(preferenceRepository.findByUserId(9L)).thenReturn(Optional.empty());

        service.retryDelivery(delivery, n);

        verify(emailSender, never()).sendSimpleNotification(any(), any(), any());
        verify(deliveryRepository, never()).updateStatus(any());
    }

    @Test
    void retryDelivery_neitherInAppNorEmailChannel_marksSentWithoutSending() {
        Notification n = notification(NotificationChannel.BOTH);
        NotificationDelivery delivery = pendingDelivery(NotificationChannel.BOTH);

        service.retryDelivery(delivery, n);

        verify(inAppNotificationSender, never()).sendToUser(any(), any());
        verify(emailSender, never()).sendSimpleNotification(any(), any(), any());
        ArgumentCaptor<NotificationDelivery> captor = ArgumentCaptor.forClass(NotificationDelivery.class);
        verify(deliveryRepository).updateStatus(captor.capture());
        assertThat(captor.getValue().status()).isEqualTo(DeliveryStatus.SENT);
    }
}
