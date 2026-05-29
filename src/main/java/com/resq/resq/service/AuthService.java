package com.resq.resq.service;

import com.resq.resq.dto.LoginRequestDTO;
import com.resq.resq.dto.RegisterRequestDTO;
import com.resq.resq.model.User;
import com.resq.resq.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.resq.resq.dto.LoginRequestDTO;

import com.resq.resq.jwt.JwtUtil;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(RegisterRequestDTO request) {

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        return userRepository.save(user);
    }
    @Autowired
    private PasswordEncoder passwordEncoder;

    public String loginUser(LoginRequestDTO request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        if (!passwordMatches) {
            throw new RuntimeException("Invalid password");
        }

        return jwtUtil.generateToken(user.getEmail());
    }

    @Autowired
    private JwtUtil jwtUtil;
}