package com.resq.resq.controller;

import com.resq.resq.dto.ReportResponseDTO;
import com.resq.resq.dto.UpdateStatusRequest;
import com.resq.resq.model.Report;
import com.resq.resq.service.VolunteerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.resq.resq.dto.SaveRescueNoteRequest;

import java.util.List;

@RestController
@RequestMapping("/api/volunteer")
public class VolunteerController {


@Autowired
private VolunteerService volunteerService;

@GetMapping("/reports")
public List<ReportResponseDTO> getAssignedReports(
        Authentication authentication) {

    String email = authentication.getName();

    return volunteerService.getAssignedReports(email);
}

@PutMapping("/reports/{id}/status")
public Report updateStatus(
        @PathVariable Long id,
        @RequestBody UpdateStatusRequest request,
        Authentication authentication) {

    String email = authentication.getName();

    return volunteerService.updateReportStatus(
            id,
            request.getStatus(),
            email
    );
}

@PutMapping("/reports/{id}/note")
public Report saveRescueNote(
        @PathVariable Long id,
        @RequestBody SaveRescueNoteRequest request,
        Authentication authentication
) {

    String email = authentication.getName();

    return volunteerService.saveRescueNote(
            id,
            request.getRescueNote(),
            email
    );
}

}
