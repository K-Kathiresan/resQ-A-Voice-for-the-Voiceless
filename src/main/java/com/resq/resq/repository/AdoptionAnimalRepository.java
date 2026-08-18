package com.resq.resq.repository;

import com.resq.resq.model.AdoptionAnimal;
import com.resq.resq.model.AdoptionStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdoptionAnimalRepository
        extends JpaRepository<AdoptionAnimal, Long> {

    List<AdoptionAnimal> findByStatus(AdoptionStatus status);
}