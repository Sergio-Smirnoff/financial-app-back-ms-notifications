package com.financialapp.notifications.infrastructure.persistence.repository;

import com.financialapp.notifications.infrastructure.persistence.entity.MonthlySummarySentSqlEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthlySummarySentSqlRepository extends JpaRepository<MonthlySummarySentSqlEntity, Long> {

    boolean existsByUserIdAndSummaryMonth(Long userId, String summaryMonth);

    long deleteByUserIdAndSummaryMonth(Long userId, String summaryMonth);
}
