package com.windsurf.gateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
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
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        // 设置响应的Content-Type为JSON
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return response
                .writeWith(Mono.fromSupplier(() -> {
                    DataBufferFactory bufferFactory = response.bufferFactory();
                    try {
                        Map<String, Object> result = new HashMap<>();
                        if (ex instanceof ResponseStatusException) {
                            ResponseStatusException responseStatusException = (ResponseStatusException) ex;
                            response.setStatusCode(responseStatusException.getStatusCode());
                            result.put("code", responseStatusException.getStatusCode().value());
                            result.put("message", responseStatusException.getMessage());
                        } else {
                            log.error("Gateway Error:", ex);
                            result.put("code", 500);
                            result.put("message", "Internal Server Error");
                        }
                        return bufferFactory.wrap(objectMapper.writeValueAsBytes(result));
                    } catch (JsonProcessingException e) {
                        log.error("Error writing response", e);
                        Map<String, Object> result = new HashMap<>();
                        result.put("code", 500);
                        result.put("message", "Internal Server Error");
                        try {
                            return bufferFactory.wrap(objectMapper.writeValueAsBytes(result));
                        } catch (JsonProcessingException ex2) {
                            return bufferFactory.wrap(new byte[0]);
                        }
                    }
                }));
    }
}
