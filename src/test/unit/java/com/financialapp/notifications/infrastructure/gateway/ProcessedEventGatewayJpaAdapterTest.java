package com.financialapp.notifications.infrastructure.gateway;

import com.financialapp.commons.messaging.domain.model.EventId;
import com.financialapp.notifications.infrastructure.persistence.entity.ProcessedEventSqlEntity;
import com.financialapp.notifications.infrastructure.persistence.repository.ProcessedEventSqlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessedEventGatewayJpaAdapterTest {

    @Mock ProcessedEventSqlRepository repository;
    @InjectMocks ProcessedEventGatewayJpaAdapter adapter;

    @Test
    void isProcessed_returnsTrueWhenExists() {
        when(repository.existsById("evt-123")).thenReturn(true);

        assertThat(adapter.isProcessed(new EventId("evt-123"))).isTrue();
    }

    @Test
    void isProcessed_returnsFalseWhenNotExists() {
        when(repository.existsById("evt-456")).thenReturn(false);

        assertThat(adapter.isProcessed(new EventId("evt-456"))).isFalse();
    }

    @Test
    void markProcessed_savesEntityWithCorrectEventId() {
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        adapter.markProcessed(new EventId("evt-789"));

        ArgumentCaptor<ProcessedEventSqlEntity> captor = ArgumentCaptor.forClass(ProcessedEventSqlEntity.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getEventId()).isEqualTo("evt-789");
        assertThat(captor.getValue().getProcessedAt()).isNotNull();
    }
}
