package com.resq.resq.controller;

import com.resq.resq.payload.ApiResponse;
import com.resq.resq.dto.ReportResponseDTO;
import com.resq.resq.model.ReportStatus;
import com.resq.resq.service.ReportService;

import jakarta.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReportResponseDTO>> createReport(

            @RequestParam("animalType")
            @NotBlank(message = "Animal type is required")
            String animalType,

            @RequestParam("description")
            @NotBlank(message = "Description is required")
            String description,

            @RequestParam("location")
            @NotBlank(message = "Location is required")
            String location,

            @RequestParam("image")
            MultipartFile image

    ) throws IOException {

        ReportResponseDTO createdReport = reportService.createReport(
                animalType,
                description,
                location,
                image
        );

        ApiResponse<ReportResponseDTO> response =
                new ApiResponse<>(
                        true,
                        "Report created successfully",
                        createdReport
                );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReportResponseDTO>>> getAllReports() {

        List<ReportResponseDTO> reports = reportService.getAllReports();

        ApiResponse<List<ReportResponseDTO>> response =
                new ApiResponse<>(
                        true,
                        "Reports fetched successfully",
                        reports
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReportResponseDTO>> getReportById(
            @PathVariable Long id
    ) {

        ReportResponseDTO report = reportService.getReportById(id);

        ApiResponse<ReportResponseDTO> response =
                new ApiResponse<>(
                        true,
                        "Report fetched successfully",
                        report
                );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ReportResponseDTO>> updateStatus(

            @PathVariable Long id,

            @RequestParam ReportStatus status

    ) {

        ReportResponseDTO updatedReport =
                reportService.updateStatus(id, status);

        ApiResponse<ReportResponseDTO> response =
                new ApiResponse<>(
                        true,
                        "Status updated successfully",
                        updatedReport
                );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteReport(
            @PathVariable Long id
    ) {

        reportService.deleteReport(id);

        ApiResponse<String> response =
                new ApiResponse<>(
                        true,
                        "Report deleted successfully",
                        null
                );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{reportId}/assign/{volunteerId}")
        public ResponseEntity<ApiResponse<ReportResponseDTO>> assignVolunteer(
                @PathVariable Long reportId,
                @PathVariable Long volunteerId
        ) {

                ReportResponseDTO updatedReport =
                        reportService.assignVolunteer(reportId, volunteerId);

                ApiResponse<ReportResponseDTO> response =
                        new ApiResponse<>(
                                true,
                                "Volunteer assigned successfully",
                                updatedReport
                        );

                return ResponseEntity.ok(response);
        }


        @PutMapping("/{id}")
        public ApiResponse<ReportResponseDTO> updateReport(
                @PathVariable Long id,
                @RequestParam String animalType,
                @RequestParam String description,
                @RequestParam String location
        ) {

        return new ApiResponse<>(
                true,
                "Report updated successfully",
                reportService.updateReport(
                        id,
                        animalType,
                        description,
                        location
                )
        );
        }
        @GetMapping("/my-reports")
        public ResponseEntity<ApiResponse<List<ReportResponseDTO>>> getMyReports() {

        List<ReportResponseDTO> reports = reportService.getMyReports();

        ApiResponse<List<ReportResponseDTO>> response =
                new ApiResponse<>(
                        true,
                        "My reports fetched successfully",
                        reports
                );

        return ResponseEntity.ok(response);
}
}