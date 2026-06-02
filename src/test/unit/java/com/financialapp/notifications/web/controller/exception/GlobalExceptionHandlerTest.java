package com.financialapp.notifications.web.controller.exception;

import com.financialapp.notifications.domain.model.exception.BusinessException;
import com.financialapp.notifications.domain.model.exception.ResourceNotFoundException;
import com.financialapp.notifications.domain.model.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleResourceNotFound_returns404WithMessage() {
        // Given a not-found exception / When handled
        ResponseEntity<ApiResponse<Void>> response =
                handler.handleResourceNotFound(new ResourceNotFoundException("Notification", 1L));

        // Then a 404 with the message body is returned
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("Notification not found with id: 1");
    }

    @Test
    void handleBusinessException_returns400WithMessage() {
        // Given a business-rule violation / When handled
        ResponseEntity<ApiResponse<Void>> response =
                handler.handleBusinessException(new BusinessException("bad rule"));

        // Then a 400 with the message body is returned
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).isEqualTo("bad rule");
    }

    @Test
    void handleValidationException_returns400WithFieldMessages() {
        // Given a binding result with one field error
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors())
                .thenReturn(List.of(new FieldError("obj", "field", "must not be null")));
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        // When handled
        ResponseEntity<ApiResponse<Void>> response = handler.handleValidationException(ex);

        // Then a 400 with the collected field messages is returned
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).isEqualTo("Validation failed");
        assertThat(response.getBody().getErrors()).containsExactly("must not be null");
    }

    @Test
    void handleGenericException_returns500() {
        // Given an unexpected exception / When handled
        ResponseEntity<ApiResponse<Void>> response = handler.handleGenericException(new RuntimeException("boom"));

        // Then a 500 with a generic message is returned
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred");
    }
}
