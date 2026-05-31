package com.financialapp.notifications.domain.model.entity.enums;

public enum NotificationChannel {
    IN_APP,
    EMAIL,
    BOTH;

    public boolean sendInApp() {
        return this == IN_APP || this == BOTH;
    }

    public boolean sendEmail() {
        return this == EMAIL || this == BOTH;
    }

}
