package com.financialapp.notifications.infrastructure.persistence.entity;

import com.financialapp.commons.messaging.infrastructure.persistence.entity.ProcessedEventEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "processed_events", schema = "notifications")
public class ProcessedEventSqlEntity extends ProcessedEventEntity {
}
