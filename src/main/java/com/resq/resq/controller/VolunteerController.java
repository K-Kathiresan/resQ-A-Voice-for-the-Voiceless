package com.resq.resq.controller;

import com.resq.resq.model.Report;
import com.resq.resq.service.VolunteerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/volunteer")
public class VolunteerController {

    @Autowired
    private VolunteerService volunteerService;

    @GetMapping("/reports")
    public List<Report> getAssignedReports(Authentication authentication) {

        String email = authentication.getName();

        return volunteerService.getAssignedReports(email);
    }
}