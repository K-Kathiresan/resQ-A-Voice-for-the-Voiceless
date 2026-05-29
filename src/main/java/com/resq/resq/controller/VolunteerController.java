package com.resq.resq.controller;

import com.resq.resq.model.Volunteer;
import com.resq.resq.payload.ApiResponse;
import com.resq.resq.service.VolunteerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/volunteers")
public class VolunteerController {

    @Autowired
    private VolunteerService volunteerService;

    @PostMapping
    public ResponseEntity<ApiResponse<Volunteer>> createVolunteer(
            @RequestBody Volunteer volunteer
    ) {

        Volunteer savedVolunteer =
                volunteerService.createVolunteer(volunteer);

        ApiResponse<Volunteer> response =
                new ApiResponse<>(
                        true,
                        "Volunteer created successfully",
                        savedVolunteer
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Volunteer>>> getAllVolunteers() {

        List<Volunteer> volunteers =
                volunteerService.getAllVolunteers();

        ApiResponse<List<Volunteer>> response =
                new ApiResponse<>(
                        true,
                        "Volunteers fetched successfully",
                        volunteers
                );

        return ResponseEntity.ok(response);
    }
}