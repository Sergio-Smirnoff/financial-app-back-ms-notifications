package com.financialapp.notifications.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisteredEvent {

    @Builder.Default
    private String eventType = "USER_REGISTERED";
    private Long userId;
    @Builder.Default
    private Instant timestamp = Instant.now();
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
