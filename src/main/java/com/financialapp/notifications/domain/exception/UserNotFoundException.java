package com.financialapp.notifications.domain.exception;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(Long userId) {
        super(DomainError.USER_NOT_FOUND, "User not found with id: " + userId);
    }
}
