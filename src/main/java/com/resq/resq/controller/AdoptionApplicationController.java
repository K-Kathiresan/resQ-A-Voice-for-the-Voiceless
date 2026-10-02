package com.resq.resq.controller;

import com.resq.resq.model.AdoptionApplication;
import com.resq.resq.model.User;
import com.resq.resq.service.AdoptionApplicationService;
import com.resq.resq.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adoption-applications")
public class AdoptionApplicationController {

    private final AdoptionApplicationService adoptionApplicationService;
    private final UserRepository userRepository;

    public AdoptionApplicationController(
            AdoptionApplicationService adoptionApplicationService,
            UserRepository userRepository) {

        this.adoptionApplicationService = adoptionApplicationService;
        this.userRepository = userRepository;
    }

    /*
     * ============================================================
     * APPLY FOR ADOPTION
     * ============================================================
     *
     * The applicant is obtained from the authenticated JWT.
     *
     * The frontend does NOT send applicantId.
     */
    @PostMapping("/apply")
    public ResponseEntity<AdoptionApplication> applyForAdoption(
            @RequestParam Long animalId,
            @RequestParam String housingType,
            @RequestParam String animalExperience,
            @RequestParam String reason,
            @RequestParam String otherPets,
            @RequestParam String contactPreference,
            Authentication authentication) {

        String email = authentication.getName();

        User applicant = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Authenticated user not found"));

        AdoptionApplication application =
                adoptionApplicationService.applyForAdoption(
                        animalId,
                        applicant.getId(),
                        housingType,
                        animalExperience,
                        reason,
                        otherPets,
                        contactPreference
                );

        return ResponseEntity.ok(application);
    }


    /*
     * ============================================================
     * REVIEW APPLICATION
     * ============================================================
     */
    @PutMapping("/{id}/review")
    public ResponseEntity<AdoptionApplication> reviewApplication(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                adoptionApplicationService.reviewApplication(id)
        );
    }


    /*
     * ============================================================
     * APPROVE APPLICATION
     * ============================================================
     */
    @PutMapping("/{id}/approve")
    public ResponseEntity<AdoptionApplication> approveApplication(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                adoptionApplicationService.approveApplication(id)
        );
    }


    /*
     * ============================================================
     * REJECT APPLICATION
     * ============================================================
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<AdoptionApplication> rejectApplication(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                adoptionApplicationService.rejectApplication(id)
        );
    }


    /*
     * ============================================================
     * GET MY APPLICATIONS
     * ============================================================
     */
    @GetMapping("/my")
    public ResponseEntity<List<AdoptionApplication>> getMyApplications(
            Authentication authentication) {

        String email = authentication.getName();

        User applicant = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Authenticated user not found"));

        return ResponseEntity.ok(
                adoptionApplicationService
                        .getApplicationsByUser(applicant.getId())
        );
    }


    /*
     * ============================================================
     * GET ALL APPLICATIONS
     * ============================================================
     *
     * SecurityConfig already protects the review/approve/reject
     * endpoints for ADMIN. We'll make the listing endpoint
     * protected too when we finalize authorization.
     */
    @GetMapping
    public ResponseEntity<List<AdoptionApplication>>
    getAllApplications() {

        return ResponseEntity.ok(
                adoptionApplicationService.getAllApplications()
        );
    }
}