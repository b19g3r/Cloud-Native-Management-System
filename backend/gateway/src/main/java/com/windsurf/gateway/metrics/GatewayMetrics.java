package com.windsurf.gateway.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Getter
public class GatewayMetrics {

    private final Counter totalRequestsCounter;
    private final Counter errorRequestsCounter;
    private final Timer requestLatencyTimer;
    private final Counter rateLimitedRequestsCounter;
    private final Counter circuitBreakerTripsCounter;

    public GatewayMetrics(MeterRegistry registry) {
        this.totalRequestsCounter = Counter.builder("gateway.requests.total")
                .description("Total number of requests processed")
                .register(registry);

        this.errorRequestsCounter = Counter.builder("gateway.requests.errors")
                .description("Total number of request errors")
                .register(registry);

        this.requestLatencyTimer = Timer.builder("gateway.requests.latency")
                .description("Request processing time")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);

        this.rateLimitedRequestsCounter = Counter.builder("gateway.ratelimit.blocked")
                .description("Number of requests blocked by rate limiting")
                .register(registry);

        this.circuitBreakerTripsCounter = Counter.builder("gateway.circuit_breaker.trips")
                .description("Number of circuit breaker trips")
                .register(registry);
    }

    public void recordRequest() {
        totalRequestsCounter.increment();
    }

    public void recordError() {
        errorRequestsCounter.increment();
    }

    public void recordLatency(long timeInMs) {
        requestLatencyTimer.record(timeInMs, TimeUnit.MILLISECONDS);
    }

    public void recordRateLimited() {
        rateLimitedRequestsCounter.increment();
    }

    public void recordCircuitBreakerTrip() {
        circuitBreakerTripsCounter.increment();
    }
}
