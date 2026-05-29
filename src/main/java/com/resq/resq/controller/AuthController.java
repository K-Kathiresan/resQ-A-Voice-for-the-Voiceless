package com.resq.resq.controller;

import com.resq.resq.dto.LoginRequestDTO;
import com.resq.resq.dto.RegisterRequestDTO;
import com.resq.resq.model.User;
import com.resq.resq.payload.ApiResponse;
import com.resq.resq.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
        public ResponseEntity<ApiResponse<String>> registerUser(
                @RequestBody RegisterRequestDTO request
        ) {

        authService.registerUser(request);

        ApiResponse<String> response =
                new ApiResponse<>(
                        true,
                        "User registered successfully",
                        "Registration completed"
                );

        return ResponseEntity.ok(response);
        }

    @PostMapping("/login")
        public ResponseEntity<ApiResponse<String>> loginUser(
                @RequestBody LoginRequestDTO request
        ) {

        String token = authService.loginUser(request);

        ApiResponse<String> response =
                new ApiResponse<>(
                        true,
                        "Login successful",
                        token
                );

        return ResponseEntity.ok(response);
        }
}