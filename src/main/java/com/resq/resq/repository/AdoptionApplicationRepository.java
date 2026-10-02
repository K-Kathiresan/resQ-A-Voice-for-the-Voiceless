package com.resq.resq.repository;

import com.resq.resq.model.AdoptionApplication;
import com.resq.resq.model.AdoptionAnimal;
import com.resq.resq.model.ApplicationStatus;
import com.resq.resq.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdoptionApplicationRepository
        extends JpaRepository<AdoptionApplication, Long> {

    // Prevent the same user from applying for the same animal twice
    boolean existsByAdoptionAnimalIdAndApplicantId(
            Long adoptionAnimalId,
            Long applicantId
    );

    // Get all applications for a particular animal
    List<AdoptionApplication> findByAdoptionAnimal(
            AdoptionAnimal adoptionAnimal
    );

    // Get all applications submitted by a particular user
    List<AdoptionApplication> findByApplicant(
            User applicant
    );

    // Get applications by animal + status
    List<AdoptionApplication> findByAdoptionAnimalAndStatus(
            AdoptionAnimal adoptionAnimal,
            ApplicationStatus status
    );

    // Get all applications with a particular status
    List<AdoptionApplication> findByStatus(
            ApplicationStatus status
    );
}