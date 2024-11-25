package com.windsurf.gateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CircuitBreakerConfig {

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()
                        .slidingWindowSize(10)
                        .failureRateThreshold(50.0f)
                        .waitDurationInOpenState(Duration.ofSeconds(10))
                        .permittedNumberOfCallsInHalfOpenState(5)
                        .build())
                .timeLimiterConfig(io.github.resilience4j.timelimiter.TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(1))
                        .build())
                .build());
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> authServiceCustomizer() {
        return factory -> factory.configure(builder -> builder
                .circuitBreakerConfig(io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()
                        .slidingWindowSize(10)
                        .failureRateThreshold(50.0f)
                        .waitDurationInOpenState(Duration.ofSeconds(10))
                        .permittedNumberOfCallsInHalfOpenState(5)
                        .build())
                .timeLimiterConfig(io.github.resilience4j.timelimiter.TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(1))
                        .build()), "auth-service");
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> systemServiceCustomizer() {
        return factory -> factory.configure(builder -> builder
                .circuitBreakerConfig(io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()
                        .slidingWindowSize(10)
                        .failureRateThreshold(50.0f)
                        .waitDurationInOpenState(Duration.ofSeconds(10))
                        .permittedNumberOfCallsInHalfOpenState(5)
                        .build())
                .timeLimiterConfig(io.github.resilience4j.timelimiter.TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(1))
                        .build()), "system-service");
    }
}
