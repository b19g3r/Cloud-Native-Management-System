package com.windsurf.gateway.filter;

import com.windsurf.gateway.metrics.GatewayMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetricsFilter implements GlobalFilter, Ordered {

    private final GatewayMetrics metrics;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();
        metrics.recordRequest();

        return chain.filter(exchange)
                .doFinally(signalType -> {
                    // 记录请求处理时间
                    long duration = System.currentTimeMillis() - startTime;
                    metrics.recordLatency(duration);

                    // 记录错误
                    HttpStatus statusCode = exchange.getResponse().getStatusCode();
                    if (statusCode != null && statusCode.is5xxServerError()) {
                        metrics.recordError();
                    }

                    // 记录限流
                    if (statusCode == HttpStatus.TOO_MANY_REQUESTS) {
                        metrics.recordRateLimited();
                    }

                    // 记录熔断
                    if (exchange.getAttribute("circuit_breaker.executed") != null) {
                        metrics.recordCircuitBreakerTrip();
                    }

                    // 记录详细日志
                    if (log.isDebugEnabled()) {
                        log.debug("Request {} {} completed with status {} in {}ms",
                                exchange.getRequest().getMethod(),
                                exchange.getRequest().getPath(),
                                statusCode,
                                duration);
                    }
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
