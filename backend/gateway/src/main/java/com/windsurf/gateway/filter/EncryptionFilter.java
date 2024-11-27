package com.windsurf.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.windsurf.gateway.config.SecurityProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class EncryptionFilter implements GlobalFilter, Ordered {

    private final SecurityProperties securityProperties;
    private final ObjectMapper objectMapper;
    private static final String ALGORITHM = "AES";
    private static final String CIPHER_MODE = "AES/ECB/PKCS5Padding";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 检查是否需要加密
        if (shouldSkipEncryption(exchange.getRequest())) {
            return chain.filter(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 处理请求解密
        ServerHttpRequestDecorator requestDecorator = new ServerHttpRequestDecorator(request) {
            @Override
            public Flux<DataBuffer> getBody() {
                return super.getBody().map(dataBuffer -> {
                    try {
                        byte[] content = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(content);
                        DataBufferUtils.release(dataBuffer);

                        // 解密数据
                        String decrypted = decrypt(new String(content, StandardCharsets.UTF_8));
                        byte[] decryptedBytes = decrypted.getBytes(StandardCharsets.UTF_8);

                        DataBufferFactory dataBufferFactory = exchange.getResponse().bufferFactory();
                        DataBuffer buffer = dataBufferFactory.wrap(decryptedBytes);
                        return buffer;
                    } catch (Exception e) {
                        log.error("Failed to decrypt request body", e);
                        return dataBuffer;
                    }
                });
            }
        };

        // 处理响应加密
        ServerHttpResponseDecorator responseDecorator = new ServerHttpResponseDecorator(response) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                    return super.writeWith(fluxBody.map(dataBuffer -> {
                        try {
                            byte[] content = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(content);
                            DataBufferUtils.release(dataBuffer);

                            // 加密数据
                            String encrypted = encrypt(new String(content, StandardCharsets.UTF_8));
                            byte[] encryptedBytes = encrypted.getBytes(StandardCharsets.UTF_8);

                            DataBufferFactory dataBufferFactory = exchange.getResponse().bufferFactory();
                            DataBuffer buffer = dataBufferFactory.wrap(encryptedBytes);
                            return buffer;
                        } catch (Exception e) {
                            log.error("Failed to encrypt response body", e);
                            return dataBuffer;
                        }
                    }));
                }
                return super.writeWith(body);
            }
        };

        return chain.filter(exchange.mutate()
                .request(requestDecorator)
                .response(responseDecorator)
                .build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private boolean shouldSkipEncryption(ServerHttpRequest request) {
        String path = request.getPath().value();
        return securityProperties.getIgnore().getUrls().stream()
                .anyMatch(path::startsWith);
    }

    private String encrypt(String content) throws Exception {
        SecretKeySpec key = new SecretKeySpec(getEncryptionKey(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(CIPHER_MODE);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(content.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    private String decrypt(String content) throws Exception {
        SecretKeySpec key = new SecretKeySpec(getEncryptionKey(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(CIPHER_MODE);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(content));
        return new String(decrypted);
    }

    private byte[] getEncryptionKey() {
        // 使用JWT密钥作为加密密钥，确保长度为16字节
        String key = securityProperties.getJwt().getSecret();
        return key.substring(0, Math.min(16, key.length())).getBytes(StandardCharsets.UTF_8);
    }
}
