package com.resq.resq.controller;

import com.resq.resq.dto.ApiResponse;
import com.resq.resq.dto.ReportRequestDTO;
import com.resq.resq.model.Report;
import com.resq.resq.service.ReportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin("*")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // CREATE REPORT
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<Report>> createReport(
            @ModelAttribute ReportRequestDTO dto) throws IOException {

        String uploadDir = System.getProperty("user.dir") + "/uploads/";

        // Create uploads folder if not exists
        File uploadFolder = new File(uploadDir);

        if (!uploadFolder.exists()) {
            uploadFolder.mkdir();
        }

        // Generate unique file name
        String fileName = UUID.randomUUID() + "_"
                + dto.getImage().getOriginalFilename();

        // Save image
        dto.getImage().transferTo(
                new File(uploadDir + fileName));

        // Create report entity
        Report report = new Report();

        report.setAnimalType(dto.getAnimalType());
        report.setDescription(dto.getDescription());
        report.setLocation(dto.getLocation());

        // Save image path
        report.setImageUrl(fileName);

        Report savedReport = reportService.saveReport(report);

        ApiResponse<Report> response =
                new ApiResponse<>(
                        true,
                        "Report created successfully",
                        savedReport
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED);
    }

    // GET ALL REPORTS
    @GetMapping
    public ResponseEntity<ApiResponse<List<Report>>> getAllReports() {

        List<Report> reports = reportService.getAllReports();

        ApiResponse<List<Report>> response =
                new ApiResponse<>(
                        true,
                        "Reports fetched successfully",
                        reports
                );

        return ResponseEntity.ok(response);
    }

    // GET REPORT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Report>> getReportById(
            @PathVariable Long id) {

        Report report = reportService.getReportById(id);

        ApiResponse<Report> response =
                new ApiResponse<>(
                        true,
                        "Report fetched successfully",
                        report
                );

        return ResponseEntity.ok(response);
    }

    // UPDATE REPORT STATUS
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Report>> updateReportStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        Report updatedReport =
                reportService.updateReportStatus(id, status);

        ApiResponse<Report> response =
                new ApiResponse<>(
                        true,
                        "Report status updated successfully",
                        updatedReport
                );

        return ResponseEntity.ok(response);
    }

    // DELETE REPORT
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteReport(
            @PathVariable Long id) {

        reportService.deleteReport(id);

        ApiResponse<String> response =
                new ApiResponse<>(
                        true,
                        "Report deleted successfully",
                        null
                );

        return ResponseEntity.ok(response);
    }
}