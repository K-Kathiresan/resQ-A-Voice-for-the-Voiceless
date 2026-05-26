package com.resq.resq.controller;

import com.resq.resq.model.Report;
import com.resq.resq.service.ReportService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.resq.resq.dto.ReportRequestDTO;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin("*")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // CREATE REPORT
    @PostMapping
        public ResponseEntity<Report> createReport(
                @Valid @RequestBody ReportRequestDTO dto) {

            Report report = new Report();

            report.setAnimalType(dto.getAnimalType());
            report.setDescription(dto.getDescription());
            report.setLocation(dto.getLocation());

            Report savedReport = reportService.saveReport(report);

        return new ResponseEntity<>(savedReport, HttpStatus.CREATED);
}

    // GET ALL REPORTS
    @GetMapping
        public ResponseEntity<List<Report>> getAllReports() {

            return ResponseEntity.ok(reportService.getAllReports());
}

        // UPDATE REPORT STATUS
    @PutMapping("/{id}/status")
        public ResponseEntity<Report> updateReportStatus(
                @PathVariable Long id,
                @RequestParam String status) {

            return ResponseEntity.ok(
                    reportService.updateReportStatus(id, status));
}

        // GET REPORT BY ID
    @GetMapping("/{id}")
        public ResponseEntity<Report> getReportById(@PathVariable Long id) {

            return ResponseEntity.ok(reportService.getReportById(id));
}
        // DELETE REPORT
    @DeleteMapping("/{id}")
        public ResponseEntity<String> deleteReport(@PathVariable Long id) {

            reportService.deleteReport(id);

            return ResponseEntity.ok("Report deleted successfully");
}
}