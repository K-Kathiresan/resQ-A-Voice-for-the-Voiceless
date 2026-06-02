package com.resq.resq.service;

import com.resq.resq.model.Report;
import com.resq.resq.model.ReportStatus;
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

    public Report updateReportStatus(
            Long reportId,
            ReportStatus status,
            String email) {

        User volunteer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Volunteer not found"));

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        if (report.getAssignedVolunteer() == null ||
                !report.getAssignedVolunteer().getId().equals(volunteer.getId())) {

            throw new RuntimeException("You are not assigned to this report");
        }

        report.setStatus(status);

        return reportRepository.save(report);
    }
}