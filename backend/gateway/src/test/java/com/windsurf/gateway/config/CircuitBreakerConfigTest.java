package com.windsurf.gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class CircuitBreakerConfigTest {

    private final CircuitBreakerConfig circuitBreakerConfig = new CircuitBreakerConfig();

    @Test
    void defaultCustomizer_ShouldConfigureDefaultSettings() {
        // When
        Customizer<ReactiveResilience4JCircuitBreakerFactory> customizer = circuitBreakerConfig.defaultCustomizer();
        ReactiveResilience4JCircuitBreakerFactory factory = new ReactiveResilience4JCircuitBreakerFactory();
        
        // Then
        assertNotNull(customizer);
        assertDoesNotThrow(() -> customizer.customize(factory));
    }

    @Test
    void authServiceCustomizer_ShouldConfigureAuthService() {
        // When
        Customizer<ReactiveResilience4JCircuitBreakerFactory> customizer = circuitBreakerConfig.authServiceCustomizer();
        ReactiveResilience4JCircuitBreakerFactory factory = new ReactiveResilience4JCircuitBreakerFactory();
        
        // Then
        assertNotNull(customizer);
        assertDoesNotThrow(() -> customizer.customize(factory));
    }

    @Test
    void systemServiceCustomizer_ShouldConfigureSystemService() {
        // When
        Customizer<ReactiveResilience4JCircuitBreakerFactory> customizer = circuitBreakerConfig.systemServiceCustomizer();
        ReactiveResilience4JCircuitBreakerFactory factory = new ReactiveResilience4JCircuitBreakerFactory();
        
        // Then
        assertNotNull(customizer);
        assertDoesNotThrow(() -> customizer.customize(factory));
    }
}
