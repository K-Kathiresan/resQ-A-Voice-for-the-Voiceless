package com.resq.resq.controller;

import com.resq.resq.model.Report;
import com.resq.resq.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin("*")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // CREATE REPORT
    @PostMapping
    public Report createReport(@RequestBody Report report) {
        return reportService.saveReport(report);
    }

    // GET ALL REPORTS
    @GetMapping
    public List<Report> getAllReports() {
        return reportService.getAllReports();
    }

        // UPDATE REPORT STATUS
    @PutMapping("/{id}/status")
    public Report updateStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        return reportService.updateReportStatus(id, status);
    }

        // GET REPORT BY ID
    @GetMapping("/{id}")
    public Report getReportById(@PathVariable Long id) {

        return reportService.getReportById(id);
    }
        // DELETE REPORT
    @DeleteMapping("/{id}")
    public String deleteReport(@PathVariable Long id) {

        return reportService.deleteReport(id);
    }
}