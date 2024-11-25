package com.windsurf.gateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Order(-1)
@Component
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        if (ex instanceof ResponseStatusException responseStatusException) {
            response.setStatusCode(responseStatusException.getStatus());
            return response.writeWith(Mono.fromSupplier(() -> {
                DataBufferFactory bufferFactory = response.bufferFactory();
                try {
                    Map<String, Object> result = new HashMap<>();
                    result.put("code", responseStatusException.getStatus().value());
                    result.put("message", responseStatusException.getReason());
                    return bufferFactory.wrap(objectMapper.writeValueAsBytes(result));
                } catch (JsonProcessingException e) {
                    log.error("Error writing response", e);
                    return bufferFactory.wrap(new byte[0]);
                }
            }));
        }

        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = response.bufferFactory();
            try {
                Map<String, Object> result = new HashMap<>();
                result.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
                result.put("message", ex.getMessage());
                return bufferFactory.wrap(objectMapper.writeValueAsBytes(result));
            } catch (JsonProcessingException e) {
                log.error("Error writing response", e);
                return bufferFactory.wrap(new byte[0]);
            }
        }));
    }
}
