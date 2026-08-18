package com.resq.resq.controller;

import com.resq.resq.model.AdoptionApplication;
import com.resq.resq.service.AdoptionApplicationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/adoption-applications")
public class AdoptionApplicationController {

    private final AdoptionApplicationService adoptionApplicationService;

    public AdoptionApplicationController(
            AdoptionApplicationService adoptionApplicationService) {
        this.adoptionApplicationService = adoptionApplicationService;
    }

    @PostMapping("/apply")
    public ResponseEntity<AdoptionApplication> applyForAdoption(
            @RequestParam Long animalId,
            @RequestParam Long applicantId,
            @RequestParam String housingType,
            @RequestParam String animalExperience,
            @RequestParam String reason,
            @RequestParam String otherPets,
            @RequestParam String contactPreference) {

        AdoptionApplication application =
                adoptionApplicationService.applyForAdoption(
                        animalId,
                        applicantId,
                        housingType,
                        animalExperience,
                        reason,
                        otherPets,
                        contactPreference
                );

        return ResponseEntity.ok(application);
    }

    @PutMapping("/{id}/review")
    public ResponseEntity<AdoptionApplication> reviewApplication(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                adoptionApplicationService.reviewApplication(id)
        );
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<AdoptionApplication> approveApplication(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                adoptionApplicationService.approveApplication(id)
        );
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<AdoptionApplication> rejectApplication(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                adoptionApplicationService.rejectApplication(id)
        );
    }
}