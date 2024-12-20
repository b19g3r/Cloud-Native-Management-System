package com.windsurf.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {
    private IgnoreProperties ignore;
    private JwtProperties jwt;

    @Data
    public static class IgnoreProperties {
        private List<String> urls;
    }

    @Data
    public static class JwtProperties {
        private String secret;
        private long expiration;
        private String header;
        private String tokenPrefix;
    }
}
