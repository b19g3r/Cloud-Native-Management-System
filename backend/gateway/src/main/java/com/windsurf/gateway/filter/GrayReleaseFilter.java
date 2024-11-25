package com.windsurf.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;

@Slf4j
@Component
public class GrayReleaseFilter implements GlobalFilter, Ordered {

    private static final String GRAY_VERSION = "gray_version";
    private static final String GRAY_WEIGHT = "gray_weight";
    private final Random random = new Random();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // 检查是否有灰度发布标记
        List<String> grayVersion = request.getHeaders().get(GRAY_VERSION);
        List<String> grayWeight = request.getHeaders().get(GRAY_WEIGHT);

        if (grayVersion != null && !grayVersion.isEmpty() && grayWeight != null && !grayWeight.isEmpty()) {
            try {
                int weight = Integer.parseInt(grayWeight.get(0));
                if (shouldRouteToGrayVersion(weight)) {
                    // 添加灰度版本标记到请求头
                    ServerHttpRequest newRequest = request.mutate()
                            .header("X-Gray-Version", grayVersion.get(0))
                            .build();
                    exchange = exchange.mutate().request(newRequest).build();
                    
                    log.debug("Request routed to gray version: {}", grayVersion.get(0));
                }
            } catch (NumberFormatException e) {
                log.warn("Invalid gray weight format: {}", grayWeight.get(0));
            }
        }

        return chain.filter(exchange);
    }

    private boolean shouldRouteToGrayVersion(int weight) {
        // weight范围：0-100
        int randomValue = random.nextInt(100);
        return randomValue < weight;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 2;
    }
}
