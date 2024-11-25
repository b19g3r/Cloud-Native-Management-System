package com.windsurf.gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = RateLimitConfig.class)
class RateLimitConfigTest {

    @Autowired
    private KeyResolver ipKeyResolver;

    @Autowired
    private KeyResolver userKeyResolver;

    @Autowired
    private KeyResolver apiKeyResolver;

    @Test
    void ipKeyResolver_ReturnsCorrectKey() {
        MockServerHttpRequest request = MockServerHttpRequest
                .get("http://localhost/test")
                .remoteAddress(new InetSocketAddress("192.168.1.1", 8080))
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Mono<String> keyMono = ipKeyResolver.resolve(exchange);

        StepVerifier.create(keyMono)
                .assertNext(key -> assertEquals("192.168.1.1", key))
                .verifyComplete();
    }

    @Test
    void userKeyResolver_WithUserId_ReturnsUserId() {
        MockServerHttpRequest request = MockServerHttpRequest
                .get("http://localhost/test")
                .header("X-User-ID", "test-user")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Mono<String> keyMono = userKeyResolver.resolve(exchange);

        StepVerifier.create(keyMono)
                .assertNext(key -> assertEquals("test-user", key))
                .verifyComplete();
    }

    @Test
    void userKeyResolver_WithoutUserId_ReturnsAnonymous() {
        MockServerHttpRequest request = MockServerHttpRequest
                .get("http://localhost/test")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Mono<String> keyMono = userKeyResolver.resolve(exchange);

        StepVerifier.create(keyMono)
                .assertNext(key -> assertEquals("anonymous", key))
                .verifyComplete();
    }

    @Test
    void apiKeyResolver_ReturnsCorrectPath() {
        MockServerHttpRequest request = MockServerHttpRequest
                .get("http://localhost/api/test")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Mono<String> keyMono = apiKeyResolver.resolve(exchange);

        StepVerifier.create(keyMono)
                .assertNext(key -> assertEquals("/api/test", key))
                .verifyComplete();
    }
}
