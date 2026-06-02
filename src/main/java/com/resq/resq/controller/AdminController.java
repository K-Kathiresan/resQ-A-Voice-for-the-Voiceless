package com.resq.resq.controller;

import com.resq.resq.model.Report;
import com.resq.resq.model.User;
import com.resq.resq.service.AdminService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/reports")
    public List<Report> getAllReports() {
        return adminService.getAllReports();
    }

    @GetMapping("/volunteers")
    public List<User> getAllVolunteers() {
        return adminService.getAllVolunteers();
    }

    @PutMapping("/reports/{reportId}/assign/{volunteerId}")
    public Report assignVolunteer(
            @PathVariable Long reportId,
            @PathVariable Long volunteerId) {

        return adminService.assignVolunteer(reportId, volunteerId);
    }
}