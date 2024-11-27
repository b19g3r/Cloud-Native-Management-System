package com.windsurf.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().name();
        String remoteAddress = request.getRemoteAddress().getAddress().getHostAddress();

        // 记录请求头
        List<String> headerInfo = new ArrayList<>();
        request.getHeaders().forEach((name, values) -> {
            if (shouldLogHeader(name)) {
                headerInfo.add(String.format("%s: %s", name, String.join(", ", values)));
            }
        });

        // 记录请求开始时间
        long startTime = System.currentTimeMillis();
        exchange.getAttributes().put("startTime", startTime);

        log.info("Incoming Request - Method: {}, Path: {}, Remote: {}, Headers: {}",
                method, path, remoteAddress, headerInfo);

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            Long startTimeAttr = exchange.getAttribute("startTime");
            if (startTimeAttr != null) {
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTimeAttr;
                log.info("Response Complete - Method: {}, Path: {}, Status: {}, Duration: {}ms",
                        method, path, exchange.getResponse().getStatusCode(), duration);
            }
        }));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private boolean shouldLogHeader(String headerName) {
        // 排除一些敏感或不必要的请求头
        List<String> excludeHeaders = List.of(
                "cookie",
                "authorization",
                "proxy-authorization",
                "x-xsrf-token"
        );
        return !excludeHeaders.contains(headerName.toLowerCase());
    }
}
