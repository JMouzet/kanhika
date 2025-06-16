package com.kanhika.controller;

import com.kanhika.dto.auth.AuthDTO;
import com.kanhika.dto.auth.LoginDTO;
import com.kanhika.dto.auth.RegisterDTO;
import com.kanhika.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthDTO> register(@RequestBody @Valid RegisterDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDTO> login(@RequestBody @Valid LoginDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
