package com.windsurf.common.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

/**
 * JwtUtils 工具类用于生成和解析 JWT 令牌。
 * <p>
 * - 生成 JWT 令牌，包含用户名和有效期。
 * - 解析 JWT 令牌，提取声明（Claims）。
 * - 验证 JWT 令牌的有效性和用户名。
 * - 检查令牌是否过期。
 */
public class JwtUtils {

    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION_TIME = 86400000; // 1 day in milliseconds

    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public static Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static boolean validateToken(String token, String username) {
        final String tokenUsername = getClaimsFromToken(token).getSubject();
        return (tokenUsername.equals(username) && !isTokenExpired(token));
    }

    private static boolean isTokenExpired(String token) {
        final Date expiration = getClaimsFromToken(token).getExpiration();
        return expiration.before(new Date());
    }
}
