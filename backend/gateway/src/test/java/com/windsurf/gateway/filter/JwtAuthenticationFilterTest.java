package com.windsurf.gateway.filter;

import com.windsurf.gateway.config.SecurityProperties;
import com.windsurf.gateway.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private SecurityProperties securityProperties;
    @Mock
    private ServerWebExchange exchange;
    @Mock
    private GatewayFilterChain chain;
    @Mock
    private ServerHttpRequest request;
    @Mock
    private ServerHttpResponse response;
    @Mock
    private SecurityProperties.JwtProperties jwtProperties;

    private JwtAuthenticationFilter filter;
    private static final String TEST_TOKEN = "test.jwt.token";
    private static final String TEST_USER_ID = "testUser";
    private static final String BEARER_PREFIX = "Bearer ";

    @BeforeEach
    void setUp() {
        when(securityProperties.getJwt()).thenReturn(jwtProperties);
        when(jwtProperties.getPrefix()).thenReturn(BEARER_PREFIX);
        when(jwtProperties.getHeader()).thenReturn(HttpHeaders.AUTHORIZATION);
        when(exchange.getRequest()).thenReturn(request);
        when(exchange.getResponse()).thenReturn(response);
        when(chain.filter(any())).thenReturn(Mono.empty());

        filter = new JwtAuthenticationFilter(jwtUtils, securityProperties);
    }

    @Test
    void filter_WhitelistedPath_SkipsAuthentication() {
        when(request.getPath()).thenReturn(() -> "/auth/login");
        when(securityProperties.getIgnore()).thenReturn(new SecurityProperties.IgnoreProperties());
        when(securityProperties.getIgnore().getUrls()).thenReturn(Arrays.asList("/auth/login", "/auth/register"));

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        verify(jwtUtils, never()).validateToken(any());
    }

    @Test
    void filter_ValidToken_AllowsRequest() {
        when(request.getPath()).thenReturn(() -> "/api/protected");
        when(request.getHeaders()).thenReturn(HttpHeaders.writableHttpHeaders(
                new HttpHeaders() {{
                    add(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + TEST_TOKEN);
                }}
        ));
        when(securityProperties.getIgnore()).thenReturn(new SecurityProperties.IgnoreProperties());
        when(securityProperties.getIgnore().getUrls()).thenReturn(Collections.emptyList());
        when(jwtUtils.validateToken(TEST_TOKEN)).thenReturn(true);
        when(jwtUtils.getUserIdFromToken(TEST_TOKEN)).thenReturn(TEST_USER_ID);

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        verify(jwtUtils).validateToken(TEST_TOKEN);
        verify(jwtUtils).getUserIdFromToken(TEST_TOKEN);
    }

    @Test
    void filter_InvalidToken_ReturnsUnauthorized() {
        when(request.getPath()).thenReturn(() -> "/api/protected");
        when(request.getHeaders()).thenReturn(HttpHeaders.writableHttpHeaders(
                new HttpHeaders() {{
                    add(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + TEST_TOKEN);
                }}
        ));
        when(securityProperties.getIgnore()).thenReturn(new SecurityProperties.IgnoreProperties());
        when(securityProperties.getIgnore().getUrls()).thenReturn(Collections.emptyList());
        when(jwtUtils.validateToken(TEST_TOKEN)).thenReturn(false);
        when(response.setStatusCode(HttpStatus.UNAUTHORIZED)).thenReturn(true);

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        verify(response).setStatusCode(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void filter_MissingToken_ReturnsUnauthorized() {
        when(request.getPath()).thenReturn(() -> "/api/protected");
        when(request.getHeaders()).thenReturn(HttpHeaders.EMPTY);
        when(securityProperties.getIgnore()).thenReturn(new SecurityProperties.IgnoreProperties());
        when(securityProperties.getIgnore().getUrls()).thenReturn(Collections.emptyList());
        when(response.setStatusCode(HttpStatus.UNAUTHORIZED)).thenReturn(true);

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        verify(response).setStatusCode(HttpStatus.UNAUTHORIZED);
    }
}
