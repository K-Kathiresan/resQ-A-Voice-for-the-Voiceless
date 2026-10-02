package com.resq.resq.controller;

import com.resq.resq.model.AdoptionAnimal;
import com.resq.resq.service.AdoptionAnimalService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adoptions")
public class AdoptionController {

    private final AdoptionAnimalService adoptionAnimalService;

    public AdoptionController(
            AdoptionAnimalService adoptionAnimalService) {
        this.adoptionAnimalService = adoptionAnimalService;
    }

    @GetMapping
    public ResponseEntity<List<AdoptionAnimal>> getAvailableAnimals() {

        return ResponseEntity.ok(
                adoptionAnimalService.getAvailableAnimals()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdoptionAnimal> getAnimalById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                adoptionAnimalService.getAnimalById(id)
        );
    }

        /*
    * ============================================================
    * CREATE ADOPTION ANIMAL
    * ============================================================
    *
    * Only ADMIN can expose a rescued animal for adoption.
    */
    @PostMapping("/create")
    public ResponseEntity<AdoptionAnimal> createAdoptionAnimal(

            @RequestParam Long reportId,
            @RequestParam String breed,
            @RequestParam String age,
            @RequestParam String gender,
            @RequestParam String healthStatus,
            @RequestParam String temperament,
            @RequestParam String description) {

        AdoptionAnimal animal =
                adoptionAnimalService.createAdoptionAnimal(
                        reportId,
                        breed,
                        age,
                        gender,
                        healthStatus,
                        temperament,
                        description
                );

        return ResponseEntity.ok(animal);
    }
        /*
    * ============================================================
    * MARK ANIMAL READY FOR ADOPTION
    * ============================================================
    *
    * Only ADMIN can make an animal publicly available.
    */
    @PutMapping("/{id}/ready")
    public ResponseEntity<AdoptionAnimal> markReadyForAdoption(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                adoptionAnimalService.markReadyForAdoption(id)
        );
    }
}