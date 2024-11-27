package com.windsurf.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response object containing JWT token information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {
    
    /**
     * JWT access token
     */
    private String token;
}
