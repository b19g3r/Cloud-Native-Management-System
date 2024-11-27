package com.windsurf.auth.service;

import com.windsurf.auth.model.LoginRequest;
import com.windsurf.auth.model.RegisterRequest;
import com.windsurf.common.core.exception.BusinessException;
import com.windsurf.common.redis.service.RedisService;
import com.windsurf.common.security.entity.User;
import com.windsurf.common.security.entity.UserStatus;
import com.windsurf.common.security.mapper.UserMapper;
import com.windsurf.common.security.properties.SecurityProperties;
import com.windsurf.common.security.util.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";
    private static final Pattern PASSWORD_PATTERN = 
        Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;
    private final SecurityProperties securityProperties;
    private final JwtUtils jwtUtils;

    @Transactional
    public String register(RegisterRequest request) {
        // Validate password confirmation
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("Passwords do not match");
        }

        // Validate password strength
        if (!isPasswordValid(request.getPassword())) {
            throw new BusinessException("Password must be at least 8 characters long and contain at least one number, " +
                    "one uppercase letter, one lowercase letter, and one special character");
        }

        // Check if username exists
        if (userMapper.findByUsername(request.getUsername()) != null) {
            throw new BusinessException("Username already exists");
        }

        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(UserStatus.ACTIVE)
                .createTime(LocalDateTime.now())
                .build();

        userMapper.insert(user);

        // Generate token
        return jwtUtils.generateToken(user);
    }

    public String login(LoginRequest request) {
        // Find user
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException("User not found");
        }

        // Check user status
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException("User account is not active");
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("Invalid password");
        }

        // Update last login time
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        // Generate token
        return jwtUtils.generateToken(user);
    }

    public String refreshToken(String token) {
        // Check if token is blacklisted
        if (isTokenBlacklisted(token)) {
            throw new BusinessException("Token has been revoked");
        }

        try {
            Claims claims = jwtUtils.getClaimsFromToken(token);
            String username = claims.getSubject();
            
            User user = userMapper.findByUsername(username);
            if (user == null || user.getStatus() != UserStatus.ACTIVE) {
                throw new BusinessException("User not found or inactive");
            }

            return jwtUtils.generateToken(user);
        } catch (Exception e) {
            throw new BusinessException("Failed to refresh token: " + e.getMessage());
        }
    }

    public void logout(String token) {
        // Add token to blacklist
        try {
            Claims claims = jwtUtils.getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            long ttl = Math.max(0, expiration.getTime() - System.currentTimeMillis());
            redisService.setCacheObject(TOKEN_BLACKLIST_PREFIX + token, "1", ttl, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new BusinessException("Failed to logout: " + e.getMessage());
        }
    }

    private boolean isTokenBlacklisted(String token) {
        return redisService.hasKey(TOKEN_BLACKLIST_PREFIX + token);
    }

    private boolean isPasswordValid(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }
}
