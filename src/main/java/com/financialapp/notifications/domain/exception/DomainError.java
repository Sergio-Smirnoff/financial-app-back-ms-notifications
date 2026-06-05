package com.financialapp.notifications.domain.exception;

import com.financialapp.commons.core.error.ErrorCategory;
import com.financialapp.commons.core.error.ErrorCode;

public enum DomainError implements ErrorCode {

    RESOURCE_NOT_FOUND(ErrorCategory.NOT_FOUND, "resource_not_found"),
    USER_NOT_FOUND(ErrorCategory.NOT_FOUND, "user_not_found"),
    BUSINESS_RULE_VIOLATION(ErrorCategory.BAD_REQUEST, "business_rule_violation"),
    INTERNAL_ERROR(ErrorCategory.INTERNAL_SERVER_ERROR, "internal_error");

    private final ErrorCategory category;
    private final String code;

    DomainError(ErrorCategory category, String code) {
        this.category = category;
        this.code = code;
    }

    @Override
    public ErrorCategory category() { return category; }

    @Override
    public String code() { return code; }
}
