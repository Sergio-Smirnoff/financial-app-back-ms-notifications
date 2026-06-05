package com.financialapp.notifications.domain.exception;

import com.financialapp.commons.core.error.DomainException;

public class BusinessException extends DomainException {

    public BusinessException(String message) {
        super(DomainError.BUSINESS_RULE_VIOLATION, message);
    }
}
