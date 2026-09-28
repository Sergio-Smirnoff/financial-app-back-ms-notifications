package com.financialapp.notifications.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "monthly_summary_sent", schema = "notifications",
        uniqueConstraints = @UniqueConstraint(name = "uq_monthly_summary_sent_user_month",
                columnNames = {"user_id", "summary_month"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlySummarySentSqlEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "summary_month", nullable = false, length = 7)
    private String summaryMonth;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;
}
