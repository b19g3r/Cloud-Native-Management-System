package com.windsurf.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.windsurf.gateway.config.SecurityProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EncryptionFilterTest {

    @Mock
    private SecurityProperties securityProperties;
    @Mock
    private SecurityProperties.JwtProperties jwtProperties;
    @Mock
    private ServerWebExchange exchange;
    @Mock
    private GatewayFilterChain chain;
    @Mock
    private ServerHttpRequest request;
    @Mock
    private ServerHttpResponse response;

    private EncryptionFilter filter;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String TEST_SECRET = "testSecretKeyWith32BytesForAES!!!!";
    private static final String TEST_CONTENT = "{\"message\":\"test\"}";

    @BeforeEach
    void setUp() {
        when(securityProperties.getJwt()).thenReturn(jwtProperties);
        when(jwtProperties.getSecret()).thenReturn(TEST_SECRET);
        when(exchange.getRequest()).thenReturn(request);
        when(exchange.getResponse()).thenReturn(response);
        when(chain.filter(any())).thenReturn(Mono.empty());

        filter = new EncryptionFilter(securityProperties, objectMapper);
    }

    @Test
    void filter_WhitelistedPath_SkipsEncryption() {
        when(request.getPath()).thenReturn(() -> "/auth/login");
        when(securityProperties.getIgnore()).thenReturn(new SecurityProperties.IgnoreProperties());
        when(securityProperties.getIgnore().getUrls()).thenReturn(Arrays.asList("/auth/login", "/auth/register"));

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();
    }

    @Test
    void filter_ValidRequest_ProcessesEncryption() {
        when(request.getPath()).thenReturn(() -> "/api/protected");
        when(securityProperties.getIgnore()).thenReturn(new SecurityProperties.IgnoreProperties());

        // 创建测试请求数据
        DataBuffer buffer = new DefaultDataBufferFactory().wrap(TEST_CONTENT.getBytes(StandardCharsets.UTF_8));
        when(request.getBody()).thenReturn(Flux.just(buffer));

        // 模拟响应数据
        when(response.bufferFactory()).thenReturn(new DefaultDataBufferFactory());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();
    }

    @Test
    void filter_ErrorInEncryption_HandlesGracefully() {
        when(request.getPath()).thenReturn(() -> "/api/protected");
        when(securityProperties.getIgnore()).thenReturn(new SecurityProperties.IgnoreProperties());

        // 创建无效的请求数据
        DataBuffer buffer = new DefaultDataBufferFactory().wrap("invalid data".getBytes(StandardCharsets.UTF_8));
        when(request.getBody()).thenReturn(Flux.just(buffer));

        // 模拟响应数据
        when(response.bufferFactory()).thenReturn(new DefaultDataBufferFactory());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();
    }

    @Test
    void filter_EmptyRequest_HandlesGracefully() {
        when(request.getPath()).thenReturn(() -> "/api/protected");
        when(securityProperties.getIgnore()).thenReturn(new SecurityProperties.IgnoreProperties());
        when(request.getBody()).thenReturn(Flux.empty());
        when(response.bufferFactory()).thenReturn(new DefaultDataBufferFactory());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();
    }

    @Test
    void filter_LargePayload_HandlesCorrectly() {
        when(request.getPath()).thenReturn(() -> "/api/protected");
        when(securityProperties.getIgnore()).thenReturn(new SecurityProperties.IgnoreProperties());

        // Create a large test payload
        StringBuilder largeContent = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            largeContent.append(TEST_CONTENT);
        }

        DataBuffer buffer = new DefaultDataBufferFactory().wrap(largeContent.toString().getBytes(StandardCharsets.UTF_8));
        when(request.getBody()).thenReturn(Flux.just(buffer));
        when(response.bufferFactory()).thenReturn(new DefaultDataBufferFactory());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();
    }

    @Test
    void filter_MultipleChunks_HandlesCorrectly() {
        when(request.getPath()).thenReturn(() -> "/api/protected");
        when(securityProperties.getIgnore()).thenReturn(new SecurityProperties.IgnoreProperties());

        DataBuffer chunk1 = new DefaultDataBufferFactory().wrap("{\"part1\":\"test\"}".getBytes(StandardCharsets.UTF_8));
        DataBuffer chunk2 = new DefaultDataBufferFactory().wrap("{\"part2\":\"test\"}".getBytes(StandardCharsets.UTF_8));
        when(request.getBody()).thenReturn(Flux.just(chunk1, chunk2));
        when(response.bufferFactory()).thenReturn(new DefaultDataBufferFactory());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();
    }

    private String encryptContent(String content) {
        try {
            return Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting content", e);
        }
    }
}
