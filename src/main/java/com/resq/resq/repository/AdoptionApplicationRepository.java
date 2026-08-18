package com.resq.resq.repository;

import com.resq.resq.model.AdoptionApplication;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AdoptionApplicationRepository
        extends JpaRepository<AdoptionApplication, Long> {

    boolean existsByAdoptionAnimalIdAndApplicantId(
            Long adoptionAnimalId,
            Long applicantId);
}