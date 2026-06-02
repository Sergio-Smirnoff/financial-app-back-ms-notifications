package com.financialapp.notifications.domain.exception;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(Long userId) {
        super("User", userId);
    }
}
