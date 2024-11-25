package com.windsurf.gateway.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    public Mono<ServerResponse> handleFallback(ServerRequest request) {
        String path = request.path();
        String serviceId = extractServiceId(path);
        
        log.warn("Service {} is unavailable, path: {}", serviceId, path);
        
        return ServerResponse
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(createFallbackResponse(serviceId)), DataBuffer.class);
    }

    private String extractServiceId(String path) {
        // 从路径中提取服务ID，例如 /auth/** -> auth-service
        String[] parts = path.split("/");
        if (parts.length > 1) {
            return parts[1] + "-service";
        }
        return "unknown-service";
    }

    private DataBuffer createFallbackResponse(String serviceId) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", HttpStatus.SERVICE_UNAVAILABLE.value());
        response.put("message", String.format("Service %s is temporarily unavailable", serviceId));
        response.put("path", serviceId);
        
        try {
            String json = objectMapper.writeValueAsString(response);
            return objectMapper.getFactory().createParser(json).getInputSource();
        } catch (JsonProcessingException e) {
            log.error("Error creating fallback response", e);
            return null;
        }
    }
}
