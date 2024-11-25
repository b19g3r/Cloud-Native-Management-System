package com.windsurf.gateway.filter;

import com.windsurf.gateway.config.JwtProperties;
import com.windsurf.gateway.config.SecurityProperties;
import com.windsurf.gateway.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtProperties jwtProperties;
    private final SecurityProperties securityProperties;
    private final JwtUtils jwtUtils;
    private final PathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 检查是否是白名单路径
        if (isIgnorePath(path)) {
            return chain.filter(exchange);
        }

        // 获取token
        String token = request.getHeaders().getFirst(jwtProperties.getHeader());
        if (token == null || !token.startsWith(jwtProperties.getPrefix())) {
            log.debug("Token is missing or invalid format for path: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 移除Bearer前缀
        token = token.substring(jwtProperties.getPrefix().length()).trim();

        // 验证token
        if (!jwtUtils.validateToken(token)) {
            log.debug("Invalid token for path: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            // 获取用户ID并添加到请求头
            String userId = jwtUtils.getUserIdFromToken(token);
            ServerHttpRequest newRequest = request.mutate()
                    .header("X-User-ID", userId)
                    .build();
            
            log.debug("User {} accessing path: {}", userId, path);
            return chain.filter(exchange.mutate().request(newRequest).build());
        } catch (Exception e) {
            log.error("Error processing token for path: {}", path, e);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private boolean isIgnorePath(String path) {
        List<String> ignoreUrls = securityProperties.getUrls();
        if (ignoreUrls == null || ignoreUrls.isEmpty()) {
            return false;
        }
        boolean isIgnored = ignoreUrls.stream()
                .anyMatch(ignoreUrl -> pathMatcher.match(ignoreUrl, path));
        if (isIgnored) {
            log.debug("Path {} is in ignore list", path);
        }
        return isIgnored;
    }

    @Override
    public int getOrder() {
        return -100; // 确保在其他过滤器之前执行
    }
}
