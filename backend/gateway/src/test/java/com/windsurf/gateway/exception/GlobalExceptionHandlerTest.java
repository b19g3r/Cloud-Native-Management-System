package com.windsurf.gateway.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void handle_ShouldHandleResponseStatusException() throws Exception {
        // Given
        MockServerHttpRequest request = MockServerHttpRequest.get("/test").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        ResponseStatusException exception = new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request");
        
        Map<String, Object> response = Map.of(
                "code", HttpStatus.BAD_REQUEST.value(),
                "message", "Bad Request"
        );
        when(objectMapper.writeValueAsBytes(any())).thenReturn("{}".getBytes());

        // When
        Mono<Void> result = globalExceptionHandler.handle(exchange, exception);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
        
        assert exchange.getResponse().getStatusCode() == HttpStatus.BAD_REQUEST;
    }

    @Test
    void handle_ShouldHandleGenericException() throws Exception {
        // Given
        MockServerHttpRequest request = MockServerHttpRequest.get("/test").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        RuntimeException exception = new RuntimeException("Internal Error");
        
        Map<String, Object> response = Map.of(
                "code", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "message", "Internal Error"
        );
        when(objectMapper.writeValueAsBytes(any())).thenReturn("{}".getBytes());

        // When
        Mono<Void> result = globalExceptionHandler.handle(exchange, exception);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
        
        assert exchange.getResponse().getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Test
    void handle_ShouldHandleJsonProcessingException() throws Exception {
        // Given
        MockServerHttpRequest request = MockServerHttpRequest.get("/test").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        RuntimeException exception = new RuntimeException("Test Error");
        
        when(objectMapper.writeValueAsBytes(any())).thenThrow(new com.fasterxml.jackson.core.JsonProcessingException("Error") {});

        // When
        Mono<Void> result = globalExceptionHandler.handle(exchange, exception);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
        
        assert exchange.getResponse().getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
