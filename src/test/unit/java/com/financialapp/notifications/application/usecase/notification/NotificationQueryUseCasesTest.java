package com.financialapp.notifications.application.usecase.notification;

import com.financialapp.notifications.domain.model.entity.Notification;
import com.financialapp.notifications.domain.model.entity.enums.NotificationChannel;
import com.financialapp.notifications.domain.model.entity.enums.NotificationType;
import com.financialapp.notifications.domain.model.exception.ResourceNotFoundException;
import com.financialapp.notifications.domain.model.response.NotificationResponse;
import com.financialapp.notifications.domain.model.response.PageResult;
import com.financialapp.notifications.domain.model.response.UnreadCountResponse;
import com.financialapp.notifications.domain.repository.NotificationRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationQueryUseCasesTest {

    @Mock NotificationRepository repository;
    @Mock NotificationMapper mapper;

    private Notification notification(Long id, Long userId) {
        return Notification.builder().id(id).userId(userId).type(NotificationType.PAYMENT_DUE)
                .title("t").message("m").channel(NotificationChannel.IN_APP).build();
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
        when(mapper.toResponse(n)).thenReturn(response(1L));

        // When executing the query
        PageResult<NotificationResponse> result = new GetNotificationUseCaseImpl(repository, mapper).execute(7L, 0, 20);

        // Then the page metadata and mapped content are carried through
        assertThat(result.content()).extracting(NotificationResponse::getId).containsExactly(1L);
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.pageNumber()).isZero();
    }

    @Test
    void getLatest_mapsTop5() {
        // Given the repo returns the latest notifications
        Notification n = notification(2L, 7L);
        when(repository.findTop5ByUserIdOrderByCreatedAtDesc(7L)).thenReturn(List.of(n));
        when(mapper.toResponse(n)).thenReturn(response(2L));

        // When executing the latest query (bankId is ignored)
        List<NotificationResponse> result = new GetLatestNotificationsUseCaseImpl(repository, mapper).execute(7L, null);

        // Then the mapped responses are returned
        assertThat(result).extracting(NotificationResponse::getId).containsExactly(2L);
    }

    @Test
    void getLatestByBank_passesBankIdAsString() {
        // Given the repo returns notifications for a bank
        Notification n = notification(3L, 7L);
        when(repository.findLatestByBank(7L, "42")).thenReturn(List.of(n));
        when(mapper.toResponse(n)).thenReturn(response(3L));

        // When executing the by-bank query
        List<NotificationResponse> result =
                new GetLatestNotificationsByBankUseCaseImpl(repository, mapper).execute(7L, 42L);

        // Then the bankId was stringified for the repo and responses mapped
        assertThat(result).extracting(NotificationResponse::getId).containsExactly(3L);
        verify(repository).findLatestByBank(7L, "42");
    }

    @Test
    void getUnreadCount_wrapsRepositoryCount() {
        // Given the repo reports 4 unread
        when(repository.countByUserIdAndReadFalse(7L)).thenReturn(4L);

        // When executing the count query
        UnreadCountResponse result = new GetUnreadCountUseCaseImpl(repository).execute(7L);

        // Then the count is wrapped in the response
        assertThat(result.getCount()).isEqualTo(4L);
    }

    @Test
    void allAsRead_delegatesToRepository() {
        // Given the all-as-read use case / When executed
        new AllAsReadUseCaseImpl(repository).execute(7L);

        // Then it delegates the mark-all to the repository
        verify(repository).markAllAsRead(7L);
    }

    @Test
    void oneAsRead_marksOwnedNotification() {
        // Given a notification owned by the user
        Notification n = notification(5L, 7L);
        when(repository.findById(5L)).thenReturn(Optional.of(n));

        // When marking it read
        new OneAsReadUseCaseImpl(repository).execute(7L, 5L);

        // Then the read copy is saved
        verify(repository).save(n.markAsRead());
    }

    @Test
    void oneAsRead_rejectsWhenOwnedByAnotherUser() {
        // Given a notification owned by a different user
        when(repository.findById(5L)).thenReturn(Optional.of(notification(5L, 999L)));

        // When marking it read / Then it is treated as not found and nothing is saved
        assertThatThrownBy(() -> new OneAsReadUseCaseImpl(repository).execute(7L, 5L))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void oneAsRead_rejectsWhenMissing() {
        // Given no such notification
        when(repository.findById(5L)).thenReturn(Optional.empty());

        // When marking it read / Then it is rejected as not found
        assertThatThrownBy(() -> new OneAsReadUseCaseImpl(repository).execute(7L, 5L))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(repository, never()).save(any());
    }
}
