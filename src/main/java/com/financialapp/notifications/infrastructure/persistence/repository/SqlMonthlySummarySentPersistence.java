package com.financialapp.notifications.infrastructure.persistence.repository;

import com.financialapp.notifications.domain.repository.MonthlySummarySentRepository;
import com.financialapp.notifications.infrastructure.persistence.entity.MonthlySummarySentSqlEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;

@Repository
@RequiredArgsConstructor
public class SqlMonthlySummarySentPersistence implements MonthlySummarySentRepository {

    private final MonthlySummarySentSqlRepository sqlRepository;
    private final Clock clock;

    @Override
    public boolean claim(Long userId, YearMonth month) {
        if (sqlRepository.existsByUserIdAndSummaryMonth(userId, month.toString())) {
            return false;
        }
        try {
            sqlRepository.saveAndFlush(MonthlySummarySentSqlEntity.builder()
                    .userId(userId)
                    .summaryMonth(month.toString())
                    .sentAt(LocalDateTime.now(clock.withZone(ZoneOffset.UTC)))
                    .build());
            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }

    @Override
    @Transactional
    public void release(Long userId, YearMonth month) {
        sqlRepository.deleteByUserIdAndSummaryMonth(userId, month.toString());
    }
}
