package com.resq.resq.service;

import com.resq.resq.dto.ReportResponseDTO;
import com.resq.resq.exception.FileValidationException;
import com.resq.resq.exception.ResourceNotFoundException;
import com.resq.resq.model.Report;
import com.resq.resq.model.ReportStatus;
import com.resq.resq.model.User;
import com.resq.resq.model.Volunteer;
import com.resq.resq.repository.ReportRepository;
import com.resq.resq.repository.UserRepository;
import com.resq.resq.repository.VolunteerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VolunteerRepository volunteerRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public ReportResponseDTO createReport(
            String animalType,
            String description,
            String location,
            MultipartFile image
    ) throws IOException {

        if (image.isEmpty()) {
            throw new FileValidationException("Image file is required");
        }

        String contentType = image.getContentType();

        if (contentType == null ||
                !(contentType.equals("image/jpeg")
                        || contentType.equals("image/png")
                        || contentType.equals("image/jpg"))) {

            throw new FileValidationException("Only JPG, JPEG, and PNG images are allowed");
        }

        long maxFileSize = 5 * 1024 * 1024;

        if (image.getSize() > maxFileSize) {
            throw new FileValidationException("File size must be less than 5 MB");
        }

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();

        File uploadPath = new File(uploadDir).getAbsoluteFile();

        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }

        File destinationFile = new File(uploadPath, fileName);

        image.transferTo(destinationFile);

        String imageUrl = "http://127.0.0.1:8080/uploads/" + fileName;

        Report report = new Report();

        report.setAnimalType(animalType);
        report.setDescription(description);
        report.setLocation(location);
        report.setImageUrl(imageUrl);
        report.setStatus(ReportStatus.PENDING);

        // IMPORTANT
        report.setUser(user);

        Report savedReport = reportRepository.save(report);

        return mapToDTO(savedReport);
    }

    public List<ReportResponseDTO> getAllReports() {

        List<Report> reports = reportRepository.findAll();

        return reports.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ReportResponseDTO getReportById(Long id) {

        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + id));

        return mapToDTO(report);
    }

    public ReportResponseDTO updateStatus(Long id, ReportStatus status) {

        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + id));

        report.setStatus(status);
        report.setUpdatedAt(LocalDateTime.now());

        Report updatedReport = reportRepository.save(report);

        return mapToDTO(updatedReport);
    }

    public ReportResponseDTO updateReport(
        Long id,
        String animalType,
        String description,
        String location
) {

    Report report = reportRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Report not found"));

    Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

    String email = authentication.getName();

    User currentUser = userRepository.findByEmail(email)
            .orElseThrow(() ->
                    new RuntimeException("User not found"));

    boolean isOwner =
            report.getUser().getId().equals(currentUser.getId());

    boolean isAdmin =
            currentUser.getRole().name().equals("ADMIN");

    if (!isOwner && !isAdmin) {
        throw new AccessDeniedException(
                "You are not allowed to update this report");
    }

    report.setAnimalType(animalType);
    report.setDescription(description);
    report.setLocation(location);
    report.setUpdatedAt(LocalDateTime.now());

    Report updatedReport = reportRepository.save(report);

    return mapToDTO(updatedReport);
}

    public void deleteReport(Long id) {

    Report report = reportRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Report not found"));

    Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

    String email = authentication.getName();

    User currentUser = userRepository.findByEmail(email)
            .orElseThrow(() ->
                    new RuntimeException("User not found"));

    boolean isOwner =
            report.getUser().getId().equals(currentUser.getId());

    boolean isAdmin =
            currentUser.getRole().name().equals("ADMIN");

    if (!isOwner && !isAdmin) {
        throw new AccessDeniedException(
                "You are not allowed to delete this report");
    }

    reportRepository.delete(report);
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

    public ReportResponseDTO assignVolunteer(Long reportId, Long volunteerId) {

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        Volunteer volunteer = volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer not found"));

        report.setVolunteer(volunteer);
        report.setStatus(ReportStatus.ASSIGNED);

        Report updatedReport = reportRepository.save(report);

        return mapToDTO(updatedReport);
    }

        public List<ReportResponseDTO> getMyReports() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Report> reports = reportRepository.findByUser(user);

        return reports.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
}