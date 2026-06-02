package com.financialapp.notifications.support;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

/**
 * Base for integration tests that need the downstream finances HTTP boundary stubbed.
 * A dynamic-port WireMock server with stub files under classpath {@code wiremock/},
 * with the WebClient base URL redirected at it via {@link DynamicPropertySource}.
 */
public abstract class WireMockIntegrationTest {

    @RegisterExtension
    protected static final WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(options().dynamicPort().usingFilesUnderClasspath("wiremock"))
            .build();

    @DynamicPropertySource
    static void downstreamUrls(DynamicPropertyRegistry registry) {
        registry.add("finances.service.url", wireMock::baseUrl);
    }
}
