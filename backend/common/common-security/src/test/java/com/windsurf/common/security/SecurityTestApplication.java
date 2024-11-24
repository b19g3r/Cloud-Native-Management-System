package com.windsurf.common.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class SecurityTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecurityTestApplication.class, args);
    }

    @RestController
    static class TestController {
        @PostMapping("/some-protected-endpoint")
        public ResponseEntity<String> someProtectedEndpoint() {
            return ResponseEntity.ok("Access granted");
        }
    }
}
