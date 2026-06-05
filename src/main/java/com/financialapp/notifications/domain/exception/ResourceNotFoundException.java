package com.financialapp.notifications.domain.exception;

import com.financialapp.commons.core.error.DomainException;
import com.financialapp.commons.core.error.ErrorCode;

public class ResourceNotFoundException extends DomainException {

    public ResourceNotFoundException(String message) {
        super(DomainError.RESOURCE_NOT_FOUND, message);
    }

    public ResourceNotFoundException(String resource, Long id) {
        super(DomainError.RESOURCE_NOT_FOUND, resource + " not found with id: " + id);
    }

    protected ResourceNotFoundException(ErrorCode error, String message) {
        super(error, message);
    }
}
