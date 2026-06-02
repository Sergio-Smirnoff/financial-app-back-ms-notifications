package com.financialapp.notifications.application.usecase.event;

import com.financialapp.notifications.domain.service.NotificationService;
import com.financialapp.notifications.application.usecase.event.impl.ProcessUserRegisteredUseCaseImpl;
import com.financialapp.notifications.domain.event.UserRegistered;
import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import com.financialapp.notifications.domain.usecase.event.command.ProcessUserRegisteredCommand;
import com.financialapp.notifications.domain.usecase.preference.CreatePreferenceIfAbsentUseCase;
import com.financialapp.notifications.domain.usecase.preference.command.CreatePreferenceIfAbsentCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProcessUserRegisteredUseCaseImplTest {

    @Mock NotificationService notificationService;
    @Mock CreatePreferenceIfAbsentUseCase createPreferenceIfAbsentUseCase;
    @InjectMocks ProcessUserRegisteredUseCaseImpl useCase;

    @Test
    void execute_createsPreferenceThenWelcomeNotification() {
        // Given a newly registered user
        UserRegistered event = new UserRegistered(8L, "a@b.com", "Ada", "L");

        // When executed
        useCase.execute(new ProcessUserRegisteredCommand(event));

        // Then the preference is created first, then a welcome notification is dispatched
        var order = inOrder(createPreferenceIfAbsentUseCase, notificationService);
        order.verify(createPreferenceIfAbsentUseCase).execute(new CreatePreferenceIfAbsentCommand(8L, "a@b.com"));
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        order.verify(notificationService).notify(captor.capture());
        Notification n = captor.getValue();
        assertThat(n.type()).isEqualTo(NotificationType.USER_REGISTERED);
        assertThat(n.title()).isEqualTo("Welcome to Financial App!");
        assertThat(n.message()).contains("Hi Ada,");
    }
}
