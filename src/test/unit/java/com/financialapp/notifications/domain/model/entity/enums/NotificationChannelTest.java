package com.financialapp.notifications.domain.model.entity.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationChannelTest {

    @Test
    void valueOf_roundTripsEveryConstant() {
        // Given the enum / When iterating values / Then valueOf round-trips each
        for (NotificationChannel channel : NotificationChannel.values()) {
            assertThat(NotificationChannel.valueOf(channel.name())).isEqualTo(channel);
        }
    }

    @ParameterizedTest
    @CsvSource({
            "IN_APP,true,false",
            "EMAIL,false,true",
            "BOTH,true,true"
    })
    void sendInAppAndSendEmail_reflectPerConstantBehavior(NotificationChannel channel, boolean inApp, boolean email) {
        // Given a channel / When asking its capabilities / Then they match the constant
        assertThat(channel.sendInApp()).isEqualTo(inApp);
        assertThat(channel.sendEmail()).isEqualTo(email);
    }
}
