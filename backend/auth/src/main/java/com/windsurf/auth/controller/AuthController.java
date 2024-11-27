package com.windsurf.auth.controller;

import com.windsurf.auth.service.AuthService;
import com.windsurf.common.core.result.R;
import com.windsurf.auth.model.LoginRequest;
import com.windsurf.auth.model.RegisterRequest;
import com.windsurf.auth.model.TokenResponse;
import com.windsurf.common.security.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;

    @PostMapping("/register")
    public R<TokenResponse> register(@Valid @RequestBody RegisterRequest request) {
        return R.ok(new TokenResponse(authService.register(request)));
    }

    @PostMapping("/login")
    public R<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return R.ok(new TokenResponse(authService.login(request)));
    }

    @PostMapping("/refresh")
    public R<TokenResponse> refresh(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        String tokenValue = jwtUtils.extractTokenFromHeader(token);
        return R.ok(new TokenResponse(authService.refreshToken(tokenValue)));
    }

    @PostMapping("/logout")
    public R<Void> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            String tokenValue = jwtUtils.extractTokenFromHeader(token);
            authService.logout(tokenValue);
            return R.ok();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token format");
        }
    }
}
