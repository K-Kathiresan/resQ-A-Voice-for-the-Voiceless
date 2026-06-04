package com.resq.resq.service;

import com.resq.resq.model.Report;
import com.resq.resq.model.ReportStatus;
import com.resq.resq.model.Role;
import com.resq.resq.model.User;

import com.resq.resq.repository.ReportRepository;
import com.resq.resq.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public List<User> getAllVolunteers() {
        return userRepository.findByRole(Role.VOLUNTEER);
    }

    public Report assignVolunteer(Long reportId, Long volunteerId) {

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        User volunteer = userRepository.findById(volunteerId)
                .orElseThrow(() -> new RuntimeException("Volunteer not found"));

        ReportStatus currentStatus = report.getStatus();

        if (currentStatus == ReportStatus.RESCUED) {

            throw new RuntimeException(
                    "Completed rescue cannot be reassigned"
            );
        }

        report.setAssignedVolunteer(volunteer);

        /*
         * Keep current status.
         *
         * Examples:
         * PENDING    -> ASSIGNED
         * ASSIGNED   -> ASSIGNED
         * ON_THE_WAY -> ON_THE_WAY
         * RESCUING   -> RESCUING
         * FAILED     -> FAILED
         */

        if (currentStatus == ReportStatus.PENDING) {
            report.setStatus(ReportStatus.ASSIGNED);
        }

        return reportRepository.save(report);
    }
}