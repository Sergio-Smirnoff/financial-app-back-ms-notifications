package com.financialapp.notifications.infrastructure.persistence.repository;

import com.financialapp.notifications.infrastructure.persistence.entity.ProcessedEventSqlEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventSqlRepository extends JpaRepository<ProcessedEventSqlEntity, String> {
}
