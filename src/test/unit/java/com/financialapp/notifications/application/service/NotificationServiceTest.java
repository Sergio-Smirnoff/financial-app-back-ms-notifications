package com.financialapp.notifications.application.service;

import com.financialapp.notifications.domain.messaging.EmailSender;
import com.financialapp.notifications.domain.messaging.InAppNotificationSender;
import com.financialapp.notifications.domain.model.entity.Notification;
import com.financialapp.notifications.domain.model.entity.UserNotificationPreference;
import com.financialapp.notifications.domain.model.entity.enums.NotificationChannel;
import com.financialapp.notifications.domain.model.entity.enums.NotificationType;
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
    @InjectMocks NotificationService service;

    private Notification notification(NotificationChannel channel) {
        return Notification.builder().id(1L).userId(9L).type(NotificationType.PAYMENT_DUE)
                .title("t").message("m").channel(channel).build();
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
                UserNotificationPreference.builder().userId(9L).email("to@x.com").build()));

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
                UserNotificationPreference.builder().userId(9L).email("to@x.com").build()));

        // When notifying
        service.notify(n);

        // Then both channels are exercised
        verify(inAppNotificationSender).sendToUser(eq(9L), eq(n));
        verify(emailSender).sendSimpleNotification("to@x.com", "t", "m");
    }
}
