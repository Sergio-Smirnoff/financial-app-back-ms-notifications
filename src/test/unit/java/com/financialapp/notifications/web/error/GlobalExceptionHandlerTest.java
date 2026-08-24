package com.financialapp.notifications.web.error;

import com.financialapp.commons.core.response.ApiResponse;
import com.financialapp.notifications.domain.exception.BusinessException;
import com.financialapp.notifications.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void resourceNotFoundMapsTo404WithCode() {
        ResponseEntity<ApiResponse<Map<String, Object>>> response =
                handler.handleDomain(new ResourceNotFoundException("Notification", 1L));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getCode()).isEqualTo("resource_not_found");
        assertThat(response.getBody().getMessage()).isEqualTo("Notification not found with id: 1");
    }

    @Test
    void businessExceptionMapsTo400WithCode() {
        ResponseEntity<ApiResponse<Map<String, Object>>> response =
                handler.handleDomain(new BusinessException("bad rule"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getCode()).isEqualTo("business_rule_violation");
        assertThat(response.getBody().getMessage()).isEqualTo("bad rule");
    }

    @Test
    void validationMapsTo400WithFieldMap() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors())
                .thenReturn(List.of(new FieldError("obj", "field", "must not be null")));
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ApiResponse<Map<String, String>>> response = handler.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getCode()).isEqualTo("validation_error");
        assertThat(response.getBody().getData()).containsEntry("field", "must not be null");
    }

    @Test
    void genericMapsTo500() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleGeneric(new RuntimeException("x"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getCode()).isEqualTo("internal_error");
    }
}
