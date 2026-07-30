package com.financialapp.notifications.infrastructure.persistence.entity;

import com.financialapp.notifications.domain.model.notification.NotificationCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_preferences", schema = "notifications", uniqueConstraints = {
        @UniqueConstraint(name = "uk_notification_pref_user_category", columnNames = {"user_id", "category"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreferenceSqlEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 30)
    private NotificationCategory category;

    @Column(name = "in_app_enabled", nullable = false)
    @Builder.Default
    private boolean inAppEnabled = true;

    @Column(name = "email_enabled", nullable = false)
    @Builder.Default
    private boolean emailEnabled = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
