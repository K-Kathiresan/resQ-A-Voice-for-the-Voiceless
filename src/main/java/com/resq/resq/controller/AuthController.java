package com.resq.resq.controller;

import com.resq.resq.dto.AuthResponseDTO;
import com.resq.resq.dto.LoginRequestDTO;
import com.resq.resq.dto.RegisterRequestDTO;
import com.resq.resq.model.User;
import com.resq.resq.payload.ApiResponse;
import com.resq.resq.repository.UserRepository;
import com.resq.resq.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

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
    public ResponseEntity<ApiResponse<AuthResponseDTO>> loginUser(
            @RequestBody LoginRequestDTO request
    ) {

        String token = authService.loginUser(request);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        AuthResponseDTO authResponseDTO =
                new AuthResponseDTO(
                        token,
                        user.getRole().name()
                );

        ApiResponse<AuthResponseDTO> response =
                new ApiResponse<>(
                        true,
                        "Login successful",
                        authResponseDTO
                );

        return ResponseEntity.ok(response);
    }
}