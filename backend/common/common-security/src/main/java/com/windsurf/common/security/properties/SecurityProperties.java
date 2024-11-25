package com.windsurf.common.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    private Jwt jwt;
    private Ignore ignore;

    @Data
    public static class Jwt {
        private String secret;
        private long expiration;
        private String header;
        private String tokenPrefix;
    }

    @Data
    public static class Ignore {
        private List<String> urls;
    }
}
