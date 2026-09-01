package com.financialapp.notifications.domain.model.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationCategoryTest {

    @ParameterizedTest
    @EnumSource(NotificationType.class)
    void everyNotificationTypeMapsToNonNullCategory(NotificationType type) {
        NotificationCategory category = NotificationCategory.forType(type);
        assertThat(category).isNotNull();
    }

    @Test
    void testSpecificCategoryMappings() {
        assertThat(NotificationCategory.forType(NotificationType.PAYMENT_DUE)).isEqualTo(NotificationCategory.PAYMENT_DUE);
        assertThat(NotificationCategory.forType(NotificationType.CARD_EXPIRING)).isEqualTo(NotificationCategory.PAYMENT_DUE);
        assertThat(NotificationCategory.forType(NotificationType.LOAN_REMINDER)).isEqualTo(NotificationCategory.PAYMENT_DUE);
        assertThat(NotificationCategory.forType(NotificationType.INSTALLMENT_REMINDER)).isEqualTo(NotificationCategory.PAYMENT_DUE);

        assertThat(NotificationCategory.forType(NotificationType.INVESTMENT_THRESHOLD)).isEqualTo(NotificationCategory.PORTFOLIO_ALERTS);
        assertThat(NotificationCategory.forType(NotificationType.MONTHLY_SUMMARY)).isEqualTo(NotificationCategory.SUMMARY);

        assertThat(NotificationCategory.forType(NotificationType.LOW_BALANCE)).isEqualTo(NotificationCategory.ACCOUNT);
        assertThat(NotificationCategory.forType(NotificationType.BALANCE_ADJUSTED)).isEqualTo(NotificationCategory.ACCOUNT);
        assertThat(NotificationCategory.forType(NotificationType.TRANSFER_SENT)).isEqualTo(NotificationCategory.ACCOUNT);
        assertThat(NotificationCategory.forType(NotificationType.TRANSFER_RECEIVED)).isEqualTo(NotificationCategory.ACCOUNT);

        assertThat(NotificationCategory.forType(NotificationType.USER_REGISTERED)).isEqualTo(NotificationCategory.SYSTEM);

        assertThat(NotificationCategory.forType(NotificationType.BUDGET_THRESHOLD_REACHED)).isEqualTo(NotificationCategory.BUDGET);
        assertThat(NotificationCategory.forType(NotificationType.IMPORT_STALE)).isEqualTo(NotificationCategory.IMPORT_HEALTH);
    }

    @Test
    void testHasUiToggleFlags() {
        assertThat(NotificationCategory.PAYMENT_DUE.hasUiToggle()).isTrue();
        assertThat(NotificationCategory.BUDGET.hasUiToggle()).isTrue();
        assertThat(NotificationCategory.PORTFOLIO_ALERTS.hasUiToggle()).isTrue();
        assertThat(NotificationCategory.SUMMARY.hasUiToggle()).isTrue();
        assertThat(NotificationCategory.IMPORT_HEALTH.hasUiToggle()).isTrue();

        assertThat(NotificationCategory.ACCOUNT.hasUiToggle()).isFalse();
        assertThat(NotificationCategory.SYSTEM.hasUiToggle()).isFalse();
    }
}
