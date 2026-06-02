package com.financialapp.notifications.domain.model.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionsTest {

    @Test
    void businessException_carriesMessageAndIsRuntime() {
        // Given a message / When constructed / Then it is a RuntimeException with that message
        BusinessException ex = new BusinessException("bad rule");
        assertThat(ex).isInstanceOf(RuntimeException.class);
        assertThat(ex.getMessage()).isEqualTo("bad rule");
    }

    @Test
    void resourceNotFound_withMessage_carriesMessage() {
        // Given a plain message / When constructed / Then it is preserved
        ResourceNotFoundException ex = new ResourceNotFoundException("missing");
        assertThat(ex.getMessage()).isEqualTo("missing");
    }

    @Test
    void resourceNotFound_withResourceAndId_buildsDescriptiveMessage() {
        // Given a resource name and id / When constructed / Then a descriptive message is built
        ResourceNotFoundException ex = new ResourceNotFoundException("Notification", 42L);
        assertThat(ex.getMessage()).isEqualTo("Notification not found with id: 42");
    }
}
