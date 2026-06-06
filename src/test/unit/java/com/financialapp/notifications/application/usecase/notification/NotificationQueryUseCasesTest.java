package com.financialapp.notifications.application.usecase.notification;

import com.financialapp.notifications.application.usecase.notification.impl.AllAsReadUseCaseImpl;
import com.financialapp.notifications.application.usecase.notification.impl.GetLatestNotificationsByBankUseCaseImpl;
import com.financialapp.notifications.application.usecase.notification.impl.GetLatestNotificationsUseCaseImpl;
import com.financialapp.notifications.application.usecase.notification.impl.GetNotificationUseCaseImpl;
import com.financialapp.notifications.application.usecase.notification.impl.GetUnreadCountUseCaseImpl;
import com.financialapp.notifications.application.usecase.notification.impl.OneAsReadUseCaseImpl;
import com.financialapp.notifications.domain.exception.BusinessException;
import com.financialapp.notifications.domain.exception.ResourceNotFoundException;
import com.financialapp.notifications.domain.model.notification.Notification;
import com.financialapp.notifications.domain.model.notification.NotificationChannel;
import com.financialapp.notifications.domain.model.notification.NotificationType;
import com.financialapp.notifications.domain.model.pagination.PageResult;
import com.financialapp.notifications.domain.repository.NotificationRepository;
import com.financialapp.notifications.domain.usecase.notification.command.AllAsReadCommand;
import com.financialapp.notifications.domain.usecase.notification.command.GetLatestNotificationsByBankCommand;
import com.financialapp.notifications.domain.usecase.notification.command.GetLatestNotificationsCommand;
import com.financialapp.notifications.domain.usecase.notification.command.GetNotificationsCommand;
import com.financialapp.notifications.domain.usecase.notification.command.GetUnreadCountCommand;
import com.financialapp.notifications.domain.usecase.notification.command.MarkOneAsReadCommand;
import com.financialapp.notifications.web.controller.dto.NotificationResponse;
import com.financialapp.notifications.web.controller.mapper.NotificationMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationQueryUseCasesTest {

    @Mock NotificationRepository repository;
    @Mock NotificationMapper mapper;

    private Notification notification(Long id, Long userId) {
        return new Notification(id, userId, NotificationType.PAYMENT_DUE,
                "t", "m", NotificationChannel.IN_APP, false, null, null);
    }

    private NotificationResponse response(Long id) {
        return NotificationResponse.builder().id(id).build();
    }

    @Test
    void getNotification_mapsRepositoryPageToResponsePage() {
        // Given a repository page of one notification
        Notification n = notification(1L, 7L);
        when(repository.findByUserIdOrderByCreatedAtDesc(7L, 0, 20))
                .thenReturn(new PageResult<>(List.of(n), 0, 20, 1));

        // When executing the query
        PageResult<Notification> result = new GetNotificationUseCaseImpl(repository).execute(new GetNotificationsCommand(7L, 0, 20));

        // Then the page metadata and content are carried through
        assertThat(result.content()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.pageNumber()).isZero();
    }

    @Test
    void getLatest_mapsTop5() {
        // Given the repo returns the latest notifications
        Notification n = notification(2L, 7L);
        when(repository.findTop5ByUserIdOrderByCreatedAtDesc(7L)).thenReturn(List.of(n));

        // When executing the latest query (bankId is ignored)
        List<Notification> result = new GetLatestNotificationsUseCaseImpl(repository).execute(new GetLatestNotificationsCommand(7L, null));

        // Then the notifications are returned
        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(2L);
    }

    @Test
    void getLatestByBank_passesBankIdAsString() {
        // Given the repo returns notifications for a bank
        Notification n = notification(3L, 7L);
        when(repository.findLatestByBank(7L, "42")).thenReturn(List.of(n));

        // When executing the by-bank query
        List<Notification> result =
                new GetLatestNotificationsByBankUseCaseImpl(repository).execute(new GetLatestNotificationsByBankCommand(7L, 42L));

        // Then the bankId was stringified for the repo and responses returned
        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(3L);
        verify(repository).findLatestByBank(7L, "42");
    }

    @Test
    void getUnreadCount_wrapsRepositoryCount() {
        // Given the repo reports 4 unread
        when(repository.countByUserIdAndReadFalse(7L)).thenReturn(4L);

        // When executing the count query
        long result = new GetUnreadCountUseCaseImpl(repository).execute(new GetUnreadCountCommand(7L));

        // Then the count is returned
        assertThat(result).isEqualTo(4L);
    }

    @Test
    void allAsRead_delegatesToRepository() {
        // Given the all-as-read use case / When executed
        new AllAsReadUseCaseImpl(repository).execute(new AllAsReadCommand(7L));

        // Then it delegates the mark-all to the repository
        verify(repository).markAllAsRead(7L);
    }

    @Test
    void oneAsRead_marksOwnedNotification() {
        // Given a notification owned by the user
        Notification n = notification(5L, 7L);
        when(repository.findById(5L)).thenReturn(Optional.of(n));

        // When marking it read
        new OneAsReadUseCaseImpl(repository).execute(new MarkOneAsReadCommand(7L, 5L));

        // Then the read copy is saved
        verify(repository).save(n.markAsRead());
    }

    @Test
    void oneAsRead_rejectsWhenOwnedByAnotherUser() {
        // Given a notification owned by a different user
        when(repository.findById(5L)).thenReturn(Optional.of(notification(5L, 999L)));

        // When marking it read / Then it is treated as not found and nothing is saved
        assertThatThrownBy(() -> new OneAsReadUseCaseImpl(repository).execute(new MarkOneAsReadCommand(7L, 5L)))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void oneAsRead_rejectsWhenMissing() {
        // Given no such notification
        when(repository.findById(5L)).thenReturn(Optional.empty());

        // When marking it read / Then it is rejected as not found
        assertThatThrownBy(() -> new OneAsReadUseCaseImpl(repository).execute(new MarkOneAsReadCommand(7L, 5L)))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void getNotification_rejectsNullUserId() {
        assertThatThrownBy(() -> new GetNotificationUseCaseImpl(repository).execute(new GetNotificationsCommand(null, 0, 20)))
                .isInstanceOf(BusinessException.class);
        verify(repository, never()).findByUserIdOrderByCreatedAtDesc(any(), anyInt(), anyInt());
    }

    @Test
    void getNotification_rejectsNonPositiveUserId() {
        assertThatThrownBy(() -> new GetNotificationUseCaseImpl(repository).execute(new GetNotificationsCommand(0L, 0, 20)))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void getNotification_rejectsNegativePage() {
        assertThatThrownBy(() -> new GetNotificationUseCaseImpl(repository).execute(new GetNotificationsCommand(7L, -1, 20)))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void getNotification_rejectsNonPositiveSize() {
        assertThatThrownBy(() -> new GetNotificationUseCaseImpl(repository).execute(new GetNotificationsCommand(7L, 0, 0)))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void getNotification_rejectsSizeAboveLimit() {
        assertThatThrownBy(() -> new GetNotificationUseCaseImpl(repository).execute(new GetNotificationsCommand(7L, 0, 101)))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void getLatest_rejectsNullUserId() {
        assertThatThrownBy(() -> new GetLatestNotificationsUseCaseImpl(repository).execute(new GetLatestNotificationsCommand(null, null)))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void getLatest_rejectsNonPositiveUserId() {
        assertThatThrownBy(() -> new GetLatestNotificationsUseCaseImpl(repository).execute(new GetLatestNotificationsCommand(-1L, null)))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void getLatestByBank_rejectsNullUserId() {
        assertThatThrownBy(() -> new GetLatestNotificationsByBankUseCaseImpl(repository).execute(new GetLatestNotificationsByBankCommand(null, 42L)))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void getLatestByBank_rejectsNonPositiveUserId() {
        assertThatThrownBy(() -> new GetLatestNotificationsByBankUseCaseImpl(repository).execute(new GetLatestNotificationsByBankCommand(0L, 42L)))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void getLatestByBank_rejectsNullBankId() {
        assertThatThrownBy(() -> new GetLatestNotificationsByBankUseCaseImpl(repository).execute(new GetLatestNotificationsByBankCommand(7L, null)))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void getLatestByBank_rejectsNonPositiveBankId() {
        assertThatThrownBy(() -> new GetLatestNotificationsByBankUseCaseImpl(repository).execute(new GetLatestNotificationsByBankCommand(7L, 0L)))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void getUnreadCount_rejectsNullUserId() {
        assertThatThrownBy(() -> new GetUnreadCountUseCaseImpl(repository).execute(new GetUnreadCountCommand(null)))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void getUnreadCount_rejectsNonPositiveUserId() {
        assertThatThrownBy(() -> new GetUnreadCountUseCaseImpl(repository).execute(new GetUnreadCountCommand(-5L)))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void allAsRead_rejectsNullUserId() {
        assertThatThrownBy(() -> new AllAsReadUseCaseImpl(repository).execute(new AllAsReadCommand(null)))
                .isInstanceOf(BusinessException.class);
        verify(repository, never()).markAllAsRead(any());
    }

    @Test
    void allAsRead_rejectsNonPositiveUserId() {
        assertThatThrownBy(() -> new AllAsReadUseCaseImpl(repository).execute(new AllAsReadCommand(0L)))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void oneAsRead_rejectsNullUserId() {
        assertThatThrownBy(() -> new OneAsReadUseCaseImpl(repository).execute(new MarkOneAsReadCommand(null, 5L)))
                .isInstanceOf(BusinessException.class);
        verify(repository, never()).findById(any());
    }

    @Test
    void oneAsRead_rejectsNonPositiveUserId() {
        assertThatThrownBy(() -> new OneAsReadUseCaseImpl(repository).execute(new MarkOneAsReadCommand(-1L, 5L)))
                .isInstanceOf(BusinessException.class);
    }
}
