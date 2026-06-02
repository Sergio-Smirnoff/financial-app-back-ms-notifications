package com.financialapp.notifications.infrastructure.messaging.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRegisteredEvent {

    @Builder.Default
    private String eventType = "USER_REGISTERED";
    private Long userId;
    private Payload payload;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Payload {
        private String email;
        private String firstName;
        private String lastName;
    }
}
