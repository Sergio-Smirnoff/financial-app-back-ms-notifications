package com.financialapp.notifications.web.controller.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InternalAuthFilterTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock FilterChain chain;
    InternalAuthFilter filter;

    @BeforeEach
    void setUp() {
        filter = new InternalAuthFilter();
        ReflectionTestUtils.setField(filter, "internalToken", "secret");
    }

    @ParameterizedTest
    @ValueSource(strings = {"/actuator/health", "/swagger-ui/index.html", "/v3/api-docs/swagger-config"})
    void bypassesExemptPrefixes(String path) throws Exception {
        // Given an exempt path / When the filter runs
        when(request.getRequestURI()).thenReturn(path);
        filter.doFilterInternal(request, response, chain);

        // Then the chain proceeds without an auth check
        verify(chain).doFilter(request, response);
        verify(response, never()).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void passesWhenTokenMatches() throws Exception {
        // Given a protected path with the matching token
        when(request.getRequestURI()).thenReturn("/api/v1/notifications");
        when(request.getHeader("X-Internal-Token")).thenReturn("secret");

        // When the filter runs / Then the chain proceeds
        filter.doFilterInternal(request, response, chain);
        verify(chain).doFilter(request, response);
        verify(response, never()).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void rejectsWhenTokenMismatch() throws Exception {
        // Given a protected path with a wrong token
        when(request.getRequestURI()).thenReturn("/api/v1/notifications");
        when(request.getHeader("X-Internal-Token")).thenReturn("wrong");
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        // When the filter runs / Then the request is rejected and the chain is not invoked
        filter.doFilterInternal(request, response, chain);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    void rejectsWhenConfiguredTokenIsEmpty() throws Exception {
        // Given the filter has an empty configured token
        ReflectionTestUtils.setField(filter, "internalToken", "");
        when(request.getRequestURI()).thenReturn("/api/v1/notifications");
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        // When the filter runs / Then the request is rejected
        filter.doFilterInternal(request, response, chain);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    void rejectsWhenConfiguredTokenIsNull() throws Exception {
        // Given the filter has no configured token (null)
        ReflectionTestUtils.setField(filter, "internalToken", null);
        when(request.getRequestURI()).thenReturn("/api/v1/notifications");
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        // When the filter runs / Then the request is rejected
        filter.doFilterInternal(request, response, chain);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(chain, never()).doFilter(any(), any());
    }
}
