package com.windsurf.gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = SecurityProperties.class)
@TestPropertySource(properties = {
        "security.ignore.urls=/api/v1/auth/login,/api/v1/auth/register",
        "security.jwt.secret=testSecret123456789012345678901234",
        "security.jwt.expiration=3600",
        "security.jwt.header=Authorization",
        "security.jwt.tokenPrefix=Bearer "
})
class SecurityPropertiesTest {

    @Autowired
    private SecurityProperties securityProperties;

    @Test
    void shouldLoadIgnoreUrls() {
        assertThat(securityProperties.getIgnore())
                .isNotNull();
        assertThat(securityProperties.getIgnore().getUrls())
                .containsExactly("/api/v1/auth/login", "/api/v1/auth/register");
    }

    @Test
    void shouldLoadJwtProperties() {
        assertThat(securityProperties.getJwt()).isNotNull();
        assertThat(securityProperties.getJwt().getSecret()).isEqualTo("testSecret123456789012345678901234");
        assertThat(securityProperties.getJwt().getExpiration()).isEqualTo(3600);
        assertThat(securityProperties.getJwt().getHeader()).isEqualTo("Authorization");
        assertThat(securityProperties.getJwt().getTokenPrefix()).isEqualTo("Bearer ");
    }
}
