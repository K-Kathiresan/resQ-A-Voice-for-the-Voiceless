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
}