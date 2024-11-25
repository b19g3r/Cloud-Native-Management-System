package com.windsurf.gateway.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class FallbackHandler {

    private final ObjectMapper objectMapper;

    public Mono<ServerResponse> handleAuthFallback(ServerRequest request) {
        return createFallbackResponse("Auth service is not available");
    }

    public Mono<ServerResponse> handleSystemFallback(ServerRequest request) {
        return createFallbackResponse("System service is not available");
    }

    private Mono<ServerResponse> createFallbackResponse(String message) {
        return ServerResponse
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.fromCallable(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("code", HttpStatus.SERVICE_UNAVAILABLE.value());
                    response.put("message", message);
                    try {
                        return objectMapper.writeValueAsBytes(response);
                    } catch (JsonProcessingException e) {
                        log.error("Error writing fallback response", e);
                        return new byte[0];
                    }
                }), byte[].class);
    }
}
