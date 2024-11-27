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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
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
    private ServerHttpResponse response;
    @Mock
    private ServerHttpRequest mockRequest;

    private EncryptionFilter filter;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String TEST_SECRET = "testSecretKeyWith32BytesForAES256!!!";
    private static final String TEST_CONTENT = "{\"message\":\"test\"}";
    private static final DefaultDataBufferFactory BUFFER_FACTORY = new DefaultDataBufferFactory();

    @BeforeEach
    void setUp() {
        when(securityProperties.getJwt()).thenReturn(jwtProperties);
        when(jwtProperties.getSecret()).thenReturn(TEST_SECRET);
        when(exchange.getResponse()).thenReturn(response);
        when(chain.filter(any())).thenReturn(Mono.empty());
        when(response.bufferFactory()).thenReturn(BUFFER_FACTORY);

        SecurityProperties.IgnoreProperties ignoreProperties = new SecurityProperties.IgnoreProperties();
        ignoreProperties.setUrls(Arrays.asList("/auth/login", "/auth/register"));
        when(securityProperties.getIgnore()).thenReturn(ignoreProperties);

        filter = new EncryptionFilter(securityProperties, objectMapper);
    }

    @Test
    void filter_WhitelistedPath_SkipsEncryption() {
        // Given
        String path = "/auth/login";
        RequestPath mockRequestPath = mock(RequestPath.class);
        when(mockRequestPath.value()).thenReturn(path);
        when(mockRequest.getPath()).thenReturn(mockRequestPath);
        when(exchange.getRequest()).thenReturn(mockRequest);

        // When & Then
        StepVerifier.create(filter.filter(exchange, chain))
                .expectComplete()
                .verify();

        verify(chain).filter(exchange);
        verifyNoMoreInteractions(response);
    }

    @Test
    void filter_ValidRequest_ProcessesEncryption() {
        // Given
        String path = "/api/protected";
        RequestPath mockRequestPath = mock(RequestPath.class);
        when(mockRequestPath.value()).thenReturn(path);
        when(mockRequest.getPath()).thenReturn(mockRequestPath);
        when(exchange.getRequest()).thenReturn(mockRequest);
        DataBuffer buffer = BUFFER_FACTORY.wrap(TEST_CONTENT.getBytes(StandardCharsets.UTF_8));
        when(mockRequest.getBody()).thenReturn(Flux.just(buffer));
        when(response.getHeaders()).thenReturn(mock(HttpHeaders.class));

        // When & Then
        StepVerifier.create(filter.filter(exchange, chain))
                .expectComplete()
                .verify();

        verify(response.getHeaders()).setContentType(MediaType.APPLICATION_JSON);
        verify(chain).filter(any());
    }

    @Test
    void filter_InvalidJson_ReturnsBadRequest() {
        // Given
        String path = "/api/protected";
        RequestPath mockRequestPath = mock(RequestPath.class);
        when(mockRequestPath.value()).thenReturn(path);
        when(mockRequest.getPath()).thenReturn(mockRequestPath);
        when(exchange.getRequest()).thenReturn(mockRequest);
        DataBuffer buffer = BUFFER_FACTORY.wrap("invalid json".getBytes(StandardCharsets.UTF_8));
        when(mockRequest.getBody()).thenReturn(Flux.just(buffer));
        when(response.setStatusCode(HttpStatus.BAD_REQUEST)).thenReturn(true);

        // When & Then
        StepVerifier.create(filter.filter(exchange, chain))
                .expectComplete()
                .verify();

        verify(response).setStatusCode(HttpStatus.BAD_REQUEST);
        verifyNoMoreInteractions(chain);
    }

    @Test
    void filter_EmptyRequest_HandlesGracefully() {
        // Given
        String path = "/api/protected";
        RequestPath mockRequestPath = mock(RequestPath.class);
        when(mockRequestPath.value()).thenReturn(path);
        when(mockRequest.getPath()).thenReturn(mockRequestPath);
        when(exchange.getRequest()).thenReturn(mockRequest);
        when(mockRequest.getBody()).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(filter.filter(exchange, chain))
                .expectComplete()
                .verify();

        verify(chain).filter(exchange);
    }

    @Test
    void filter_LargePayload_HandlesCorrectly() {
        // Given
        String path = "/api/protected";
        RequestPath mockRequestPath = mock(RequestPath.class);
        when(mockRequestPath.value()).thenReturn(path);
        when(mockRequest.getPath()).thenReturn(mockRequestPath);
        when(exchange.getRequest()).thenReturn(mockRequest);
        StringBuilder largeContent = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            largeContent.append(TEST_CONTENT);
        }

        DataBuffer buffer = BUFFER_FACTORY.wrap(largeContent.toString().getBytes(StandardCharsets.UTF_8));
        when(mockRequest.getBody()).thenReturn(Flux.just(buffer));
        when(response.getHeaders()).thenReturn(mock(HttpHeaders.class));

        // When & Then
        StepVerifier.create(filter.filter(exchange, chain))
                .expectComplete()
                .verify();

        verify(chain).filter(any());
    }

    @Test
    void filter_MultipleChunks_HandlesCorrectly() {
        // Given
        String path = "/api/protected";
        RequestPath mockRequestPath = mock(RequestPath.class);
        when(mockRequestPath.value()).thenReturn(path);
        when(mockRequest.getPath()).thenReturn(mockRequestPath);
        when(exchange.getRequest()).thenReturn(mockRequest);
        DataBuffer chunk1 = BUFFER_FACTORY.wrap("{\"part1\":\"test\"}".getBytes(StandardCharsets.UTF_8));
        DataBuffer chunk2 = BUFFER_FACTORY.wrap("{\"part2\":\"test\"}".getBytes(StandardCharsets.UTF_8));
        when(mockRequest.getBody()).thenReturn(Flux.just(chunk1, chunk2));
        when(response.getHeaders()).thenReturn(mock(HttpHeaders.class));

        // When & Then
        StepVerifier.create(filter.filter(exchange, chain))
                .expectComplete()
                .verify();

        verify(chain).filter(any());
    }

    private String encryptContent(String content) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(TEST_SECRET.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(content.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting content", e);
        }
    }

    private String decryptContent(String encryptedContent) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(TEST_SECRET.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedContent));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting content", e);
        }
    }
}