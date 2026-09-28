package com.financialapp.notifications.infrastructure.config;

import org.junit.jupiter.api.Test;

import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class ClockConfigTest {

    @Test
    void clock_runsInTheConfiguredZone() {
        assertThat(new ClockConfig().clock("America/Argentina/Buenos_Aires").getZone())
                .isEqualTo(ZoneId.of("America/Argentina/Buenos_Aires"));
    }
}
