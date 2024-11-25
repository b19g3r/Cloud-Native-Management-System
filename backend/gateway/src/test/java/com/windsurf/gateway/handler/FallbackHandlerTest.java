package com.windsurf.gateway.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FallbackHandlerTest {

    private FallbackHandler fallbackHandler;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        fallbackHandler = new FallbackHandler(objectMapper);
    }

    @Test
    void handleAuthFallback_ShouldReturnServiceUnavailable() throws Exception {
        // Given
        ServerRequest request = MockServerRequest.builder().build();
        byte[] responseBytes = "{\"code\":503,\"message\":\"Auth service is not available\"}".getBytes();
        when(objectMapper.writeValueAsBytes(any())).thenReturn(responseBytes);

        // When
        Mono<?> response = fallbackHandler.handleAuthFallback(request);

        // Then
        StepVerifier.create(response)
                .expectNextMatches(res -> {
                    var serverResponse = (org.springframework.web.reactive.function.server.ServerResponse) res;
                    return serverResponse.statusCode() == HttpStatus.SERVICE_UNAVAILABLE
                            && serverResponse.headers().getContentType() == MediaType.APPLICATION_JSON;
                })
                .verifyComplete();
    }

    @Test
    void handleSystemFallback_ShouldReturnServiceUnavailable() throws Exception {
        // Given
        ServerRequest request = MockServerRequest.builder().build();
        byte[] responseBytes = "{\"code\":503,\"message\":\"System service is not available\"}".getBytes();
        when(objectMapper.writeValueAsBytes(any())).thenReturn(responseBytes);

        // When
        Mono<?> response = fallbackHandler.handleSystemFallback(request);

        // Then
        StepVerifier.create(response)
                .expectNextMatches(res -> {
                    var serverResponse = (org.springframework.web.reactive.function.server.ServerResponse) res;
                    return serverResponse.statusCode() == HttpStatus.SERVICE_UNAVAILABLE
                            && serverResponse.headers().getContentType() == MediaType.APPLICATION_JSON;
                })
                .verifyComplete();
    }

    @Test
    void handleFallback_ShouldHandleJsonProcessingException() throws Exception {
        // Given
        ServerRequest request = MockServerRequest.builder().build();
        when(objectMapper.writeValueAsBytes(any())).thenThrow(new com.fasterxml.jackson.core.JsonProcessingException("Test error") {});

        // When
        Mono<?> response = fallbackHandler.handleAuthFallback(request);

        // Then
        StepVerifier.create(response)
                .expectNextMatches(res -> {
                    var serverResponse = (org.springframework.web.reactive.function.server.ServerResponse) res;
                    return serverResponse.statusCode() == HttpStatus.SERVICE_UNAVAILABLE
                            && serverResponse.headers().getContentType() == MediaType.APPLICATION_JSON;
                })
                .verifyComplete();
    }
}
