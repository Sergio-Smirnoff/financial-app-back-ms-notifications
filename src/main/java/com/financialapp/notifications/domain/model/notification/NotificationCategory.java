package com.financialapp.notifications.domain.model.notification;

public enum NotificationCategory {
    PAYMENT_DUE,
    BUDGET,
    PORTFOLIO_ALERTS,
    SUMMARY,
    IMPORT_HEALTH,
    ACCOUNT,
    SYSTEM;

    public static NotificationCategory forType(NotificationType type) {
        return switch (type) {
            case PAYMENT_DUE, CARD_EXPIRING, LOAN_REMINDER, INSTALLMENT_REMINDER -> PAYMENT_DUE;
            case INVESTMENT_THRESHOLD -> PORTFOLIO_ALERTS;
            case MONTHLY_SUMMARY -> SUMMARY;
            case LOW_BALANCE, BALANCE_ADJUSTED, TRANSFER_SENT, TRANSFER_RECEIVED -> ACCOUNT;
            case USER_REGISTERED -> SYSTEM;
            case BUDGET_THRESHOLD_REACHED -> BUDGET;
            case IMPORT_STALE -> IMPORT_HEALTH;
        };
    }

    public boolean hasUiToggle() {
        return this == PAYMENT_DUE
                || this == BUDGET
                || this == PORTFOLIO_ALERTS
                || this == SUMMARY
                || this == IMPORT_HEALTH;
    }
}
