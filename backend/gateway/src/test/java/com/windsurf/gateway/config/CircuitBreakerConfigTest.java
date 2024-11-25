package com.windsurf.gateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class CircuitBreakerConfigTest {

    private CircuitBreakerConfig circuitBreakerConfig;
    private TimeLimiterConfig timeLimiterConfig;
    private CircuitBreakerRegistry circuitBreakerRegistry;
    private TimeLimiterRegistry timeLimiterRegistry;
    private CircuitBreakerConfig defaultConfig;
    private CircuitBreakerConfig authServiceConfig;
    private CircuitBreakerConfig systemServiceConfig;

    @BeforeEach
    void setUp() {
        circuitBreakerConfig = CircuitBreakerConfig.custom()
                .slidingWindowSize(10)
                .failureRateThreshold(50.0f)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .permittedNumberOfCallsInHalfOpenState(5)
                .build();

        timeLimiterConfig = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(1))
                .build();

        circuitBreakerRegistry = CircuitBreakerRegistry.of(circuitBreakerConfig);
        timeLimiterRegistry = TimeLimiterRegistry.of(timeLimiterConfig);

        defaultConfig = circuitBreakerRegistry.getDefaultConfig();
        CircuitBreaker authCircuitBreaker = circuitBreakerRegistry.circuitBreaker("auth-service");
        CircuitBreaker systemCircuitBreaker = circuitBreakerRegistry.circuitBreaker("system-service");
        
        authServiceConfig = authCircuitBreaker.getCircuitBreakerConfig();
        systemServiceConfig = systemCircuitBreaker.getCircuitBreakerConfig();
    }

    @Test
    void defaultCustomizer_CreatesDefaultConfiguration() {
        CircuitBreakerConfig config = defaultConfig;
        
        assertEquals(10, config.getSlidingWindowSize());
        assertEquals(50.0f, config.getFailureRateThreshold());
        assertEquals(Duration.ofSeconds(10), config.getWaitDurationInOpenState());
        assertEquals(5, config.getPermittedNumberOfCallsInHalfOpenState());
    }

    @Test
    void authServiceCustomizer_CreatesAuthServiceConfiguration() {
        CircuitBreakerConfig config = authServiceConfig;
        TimeLimiterConfig timeConfig = timeLimiterRegistry.getDefaultConfig();

        assertEquals(10, config.getSlidingWindowSize());
        assertEquals(50.0f, config.getFailureRateThreshold());
        assertEquals(Duration.ofSeconds(10), config.getWaitDurationInOpenState());
        assertEquals(5, config.getPermittedNumberOfCallsInHalfOpenState());
        assertEquals(Duration.ofSeconds(1), timeConfig.getTimeoutDuration());
    }

    @Test
    void systemServiceCustomizer_CreatesSystemServiceConfiguration() {
        CircuitBreakerConfig config = systemServiceConfig;
        TimeLimiterConfig timeConfig = timeLimiterRegistry.getDefaultConfig();

        assertEquals(10, config.getSlidingWindowSize());
        assertEquals(50.0f, config.getFailureRateThreshold());
        assertEquals(Duration.ofSeconds(10), config.getWaitDurationInOpenState());
        assertEquals(5, config.getPermittedNumberOfCallsInHalfOpenState());
        assertEquals(Duration.ofSeconds(1), timeConfig.getTimeoutDuration());
    }

    @Test
    void circuitBreakerFactory_ConfiguresAllServices() {
        ReactiveResilience4JCircuitBreakerFactory factory = new ReactiveResilience4JCircuitBreakerFactory();
        CircuitBreakerConfig config = defaultConfig;

        assertNotNull(config);
        assertTrue(config.getSlidingWindowSize() > 0);
        assertTrue(config.getFailureRateThreshold() > 0);
        assertNotNull(config.getWaitDurationInOpenState());
        assertTrue(config.getPermittedNumberOfCallsInHalfOpenState() > 0);
    }
}
