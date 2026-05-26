package com.resq.resq.service;

import com.resq.resq.model.Report;
import com.resq.resq.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.resq.resq.exception.ResourceNotFoundException;

import java.util.List;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    // SAVE REPORT
    public Report saveReport(Report report) {
        return reportRepository.save(report);
    }

    // GET ALL REPORTS
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

        // UPDATE REPORT STATUS
    public Report updateReportStatus(Long id, String status) {

        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with ID: " + id));

        report.setStatus(status);

        return reportRepository.save(report);
    }
        // GET REPORT BY ID
    public Report getReportById(Long id) {

        return reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with ID: " + id));
    }
        // DELETE REPORT
    public String deleteReport(Long id) {

        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with ID: " + id));

        reportRepository.delete(report);

        return "Report deleted successfully";
    }
    
}
