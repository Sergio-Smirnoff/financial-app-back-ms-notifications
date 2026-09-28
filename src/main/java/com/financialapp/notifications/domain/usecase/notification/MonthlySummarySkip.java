package com.financialapp.notifications.domain.usecase.notification;

public enum MonthlySummarySkip {
    ALREADY_SENT("already-sent"),
    OPTED_OUT("opted-out"),
    NO_RECIPIENT("no-recipient");

    private final String code;

    MonthlySummarySkip(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }
}
