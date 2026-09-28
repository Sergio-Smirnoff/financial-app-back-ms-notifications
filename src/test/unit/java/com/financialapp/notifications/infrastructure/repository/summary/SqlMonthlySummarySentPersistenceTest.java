package com.financialapp.notifications.infrastructure.repository.summary;

import com.financialapp.notifications.infrastructure.persistence.entity.MonthlySummarySentSqlEntity;
import com.financialapp.notifications.infrastructure.persistence.repository.MonthlySummarySentSqlRepository;
import com.financialapp.notifications.infrastructure.persistence.repository.SqlMonthlySummarySentPersistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SqlMonthlySummarySentPersistenceTest {

    private static final YearMonth AUGUST = YearMonth.of(2026, 8);
    private static final Clock CLOCK =
            Clock.fixed(Instant.parse("2026-09-22T11:30:00Z"), ZoneId.of("America/Argentina/Buenos_Aires"));

    @Mock private MonthlySummarySentSqlRepository sqlRepository;
    private SqlMonthlySummarySentPersistence persistence;

    @BeforeEach
    void setUp() {
        persistence = new SqlMonthlySummarySentPersistence(sqlRepository, CLOCK);
    }

    @Test
    void claim_existingRecord_isRefused() {
        when(sqlRepository.existsByUserIdAndSummaryMonth(7L, "2026-08")).thenReturn(true);

        assertThat(persistence.claim(7L, AUGUST)).isFalse();
        verify(sqlRepository, never()).saveAndFlush(any());
    }

    @Test
    void claim_newRecord_isWrittenWithTheUtcTime() {
        when(sqlRepository.existsByUserIdAndSummaryMonth(7L, "2026-08")).thenReturn(false);

        assertThat(persistence.claim(7L, AUGUST)).isTrue();
        ArgumentCaptor<MonthlySummarySentSqlEntity> saved = ArgumentCaptor.forClass(MonthlySummarySentSqlEntity.class);
        verify(sqlRepository).saveAndFlush(saved.capture());
        assertThat(saved.getValue().getUserId()).isEqualTo(7L);
        assertThat(saved.getValue().getSummaryMonth()).isEqualTo("2026-08");
        assertThat(saved.getValue().getSentAt()).isEqualTo(LocalDateTime.of(2026, 9, 22, 11, 30));
    }

    @Test
    void claim_lostRaceToAnotherCaller_isRefused() {
        when(sqlRepository.existsByUserIdAndSummaryMonth(7L, "2026-08")).thenReturn(false);
        when(sqlRepository.saveAndFlush(any())).thenThrow(new DataIntegrityViolationException("uq"));

        assertThat(persistence.claim(7L, AUGUST)).isFalse();
    }

    @Test
    void release_deletesThatUsersMonth() {
        persistence.release(7L, AUGUST);

        verify(sqlRepository).deleteByUserIdAndSummaryMonth(7L, "2026-08");
    }
}
