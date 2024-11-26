package com.windsurf.auth.controller;

import com.windsurf.auth.service.AuthService;
import com.windsurf.common.core.result.R;
import com.windsurf.auth.model.LoginRequest;
import com.windsurf.auth.model.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public R<String> register(@Valid @RequestBody RegisterRequest request) {
        return R.ok(authService.register(request));
    }

    @PostMapping("/login")
    public R<String> login(@Valid @RequestBody LoginRequest request) {
        return R.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public R<String> refresh(@RequestHeader("Authorization") String token) {
        return R.ok(authService.refreshToken(token));
    }

    @PostMapping("/logout")
    public R<Void> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return R.ok();
    }
}
