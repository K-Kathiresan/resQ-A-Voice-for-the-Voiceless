package com.resq.resq.service;

import com.resq.resq.model.Report;
import com.resq.resq.model.User;
import com.resq.resq.repository.ReportRepository;
import com.resq.resq.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VolunteerService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Report> getAssignedReports(String email) {

        User volunteer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Volunteer not found"));

        return reportRepository.findByAssignedVolunteer(volunteer);
    }
}