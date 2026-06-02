package com.financialapp.notifications.domain.model.response;

import com.financialapp.notifications.web.controller.dto.ApiResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    @Test
    void ok_withData_succeedsWithDefaultMessageAndTimestamp() {
        // Given / When
        ApiResponse<String> response = ApiResponse.ok("payload");

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("OK");
        assertThat(response.getData()).isEqualTo("payload");
        assertThat(response.getErrors()).isNull();
        assertThat(response.getTimestamp()).isNotNull();
    }

    @Test
    void ok_withMessageAndData_carriesMessage() {
        // Given / When
        ApiResponse<String> response = ApiResponse.ok("done", "payload");

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("done");
        assertThat(response.getData()).isEqualTo("payload");
    }

    @Test
    void error_withMessage_failsWithNoErrors() {
        // Given / When
        ApiResponse<Void> response = ApiResponse.error("boom");

        // Then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("boom");
        assertThat(response.getErrors()).isNull();
        assertThat(response.getData()).isNull();
    }

    @Test
    void error_withMessageAndErrors_carriesErrorList() {
        // Given a list of field errors / When building an error response
        ApiResponse<Void> response = ApiResponse.error("invalid", List.of("a", "b"));

        // Then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("invalid");
        assertThat(response.getErrors()).containsExactly("a", "b");
    }
}
