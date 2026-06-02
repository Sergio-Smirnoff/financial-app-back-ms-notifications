package com.financialapp.notifications.domain.model.exception;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(Long userId) {
        super("User", userId);
    }
}
