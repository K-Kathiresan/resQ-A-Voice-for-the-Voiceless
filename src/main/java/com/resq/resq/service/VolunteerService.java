package com.resq.resq.service;

import com.resq.resq.dto.ReportResponseDTO;
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

public List<ReportResponseDTO> getAssignedReports(String email) {

    User volunteer = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Volunteer not found"));

    List reports =
            reportRepository.findByAssignedVolunteer(volunteer);

    return reports.stream()
            .map(this::mapToDTO)
            .toList();
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
            !report.getAssignedVolunteer()
                    .getId()
                    .equals(volunteer.getId())) {

        throw new RuntimeException(
                "You are not assigned to this report"
        );
    }

    report.setStatus(status);

    return reportRepository.save(report);
}

private ReportResponseDTO mapToDTO(Report report) {

    ReportResponseDTO dto = new ReportResponseDTO();

    dto.setId(report.getId());

    dto.setAnimalType(report.getAnimalType());

    dto.setDescription(report.getDescription());

    dto.setLocation(report.getLocation());

    dto.setStatus(report.getStatus());

    dto.setImageUrl(report.getImageUrl());

    dto.setCreatedAt(report.getCreatedAt());

    dto.setUpdatedAt(report.getUpdatedAt());

    return dto;
}


}
