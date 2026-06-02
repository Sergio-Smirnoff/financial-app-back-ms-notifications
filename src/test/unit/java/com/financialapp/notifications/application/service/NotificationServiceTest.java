package com.financialapp.notifications.application.service;

import com.financialapp.notifications.domain.messaging.EmailSender;
import com.financialapp.notifications.domain.messaging.InAppNotificationSender;
import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import com.financialapp.notifications.domain.model.notification.UserNotificationPreference;
import com.financialapp.notifications.domain.repository.NotificationRepository;
import com.financialapp.notifications.domain.repository.UserNotificationPreferenceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    @InjectMocks NotificationServiceImpl service;

    private Notification notification(NotificationChannel channel) {
        return new Notification(1L, 9L, NotificationType.PAYMENT_DUE, "t", "m", channel, false, null, null);
    }

    @Test
    void notify_inAppChannel_savesAndSendsInAppOnly() {
        // Given an IN_APP notification that the repo echoes back on save
        Notification n = notification(NotificationChannel.IN_APP);
        when(notificationRepository.save(n)).thenReturn(n);

        // When notifying
        service.notify(n);

        // Then it is persisted and pushed in-app, with no email lookup
        verify(notificationRepository).save(n);
        verify(inAppNotificationSender).sendToUser(9L, n);
        verifyNoInteractions(emailSender);
        verify(preferenceRepository, never()).findByUserId(any());
    }

    @Test
    void notify_emailChannel_savesAndEmailsWhenPreferenceExists() {
        // Given an EMAIL notification and an existing preference
        Notification n = notification(NotificationChannel.EMAIL);
        when(notificationRepository.save(n)).thenReturn(n);
        when(preferenceRepository.findByUserId(9L)).thenReturn(Optional.of(
                new UserNotificationPreference(null, 9L, "to@x.com", true, null, null)));

        // When notifying
        service.notify(n);

        // Then the email is sent and nothing is pushed in-app
        verify(emailSender).sendSimpleNotification("to@x.com", "t", "m");
        verify(inAppNotificationSender, never()).sendToUser(any(), any());
    }

    @Test
    void notify_emailChannel_skipsEmailWhenNoPreference() {
        // Given an EMAIL notification but no stored preference
        Notification n = notification(NotificationChannel.EMAIL);
        when(notificationRepository.save(n)).thenReturn(n);
        when(preferenceRepository.findByUserId(9L)).thenReturn(Optional.empty());

        // When notifying
        service.notify(n);

        // Then no email is sent
        verify(emailSender, never()).sendSimpleNotification(any(), any(), any());
    }

    @Test
    void notify_bothChannel_sendsInAppAndEmail() {
        // Given a BOTH notification and an existing preference
        Notification n = notification(NotificationChannel.BOTH);
        when(notificationRepository.save(n)).thenReturn(n);
        when(preferenceRepository.findByUserId(9L)).thenReturn(Optional.of(
                new UserNotificationPreference(null, 9L, "to@x.com", true, null, null)));

        // When notifying
        service.notify(n);

        // Then both channels are exercised
        verify(inAppNotificationSender).sendToUser(eq(9L), eq(n));
        verify(emailSender).sendSimpleNotification("to@x.com", "t", "m");
    }
}
