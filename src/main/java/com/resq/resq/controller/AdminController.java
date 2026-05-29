package com.resq.resq.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/test")
    public String adminTest() {
        return "Welcome Admin!";
    }

    @GetMapping("/me")
    public String currentUser() {

        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }
}