package com.windsurf.common.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {
    
    private JwtProperties jwt = new JwtProperties();
    private IgnoreProperties ignore = new IgnoreProperties();

    @Data
    public static class JwtProperties {
        private String secret = "your-secret-key";
        private long expiration = 86400; // 24小时
        private String tokenPrefix = "Bearer ";
        private String header = "Authorization";
    }

    @Data
    public static class IgnoreProperties {
        private List<String> urls;
    }
}
