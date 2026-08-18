package com.resq.resq.service;

import com.resq.resq.model.AdoptionAnimal;
import com.resq.resq.model.AdoptionApplication;
import com.resq.resq.model.AdoptionStatus;
import com.resq.resq.model.ApplicationStatus;
import com.resq.resq.model.User;
import com.resq.resq.repository.AdoptionAnimalRepository;
import com.resq.resq.repository.AdoptionApplicationRepository;
import com.resq.resq.repository.UserRepository;

import org.springframework.stereotype.Service;

@Service
public class AdoptionApplicationService {

    private final AdoptionApplicationRepository adoptionApplicationRepository;
    private final AdoptionAnimalRepository adoptionAnimalRepository;
    private final UserRepository userRepository;

    public AdoptionApplicationService(
            AdoptionApplicationRepository adoptionApplicationRepository,
            AdoptionAnimalRepository adoptionAnimalRepository,
            UserRepository userRepository) {

        this.adoptionApplicationRepository = adoptionApplicationRepository;
        this.adoptionAnimalRepository = adoptionAnimalRepository;
        this.userRepository = userRepository;
    }

    public AdoptionApplication applyForAdoption(
            Long animalId,
            Long applicantId,
            String housingType,
            String animalExperience,
            String reason,
            String otherPets,
            String contactPreference) {

        AdoptionAnimal animal = adoptionAnimalRepository.findById(animalId)
                .orElseThrow(() ->
                        new RuntimeException("Adoption animal not found"));

        if (animal.getStatus() != AdoptionStatus.READY_FOR_ADOPTION) {
            throw new RuntimeException(
                    "This animal is not currently available for adoption"
            );
        }

        User applicant = userRepository.findById(applicantId)
                .orElseThrow(() ->
                        new RuntimeException("Applicant not found"));

        if (adoptionApplicationRepository
                .existsByAdoptionAnimalIdAndApplicantId(animalId, applicantId)) {

            throw new RuntimeException(
                    "You have already applied for this animal"
            );
        }

        AdoptionApplication application = new AdoptionApplication();

        application.setAdoptionAnimal(animal);
        application.setApplicant(applicant);
        application.setHousingType(housingType);
        application.setAnimalExperience(animalExperience);
        application.setReason(reason);
        application.setOtherPets(otherPets);
        application.setContactPreference(contactPreference);

        application.setStatus(ApplicationStatus.SUBMITTED);

        return adoptionApplicationRepository.save(application);
    }

    public AdoptionApplication reviewApplication(Long applicationId) {

        AdoptionApplication application =
                adoptionApplicationRepository.findById(applicationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Adoption application not found"));

        if (application.getStatus() != ApplicationStatus.SUBMITTED) {
            throw new RuntimeException(
                    "Only submitted applications can be reviewed"
            );
        }

        application.setStatus(ApplicationStatus.UNDER_REVIEW);

        return adoptionApplicationRepository.save(application);
    }

    public AdoptionApplication approveApplication(Long applicationId) {

        AdoptionApplication application =
                adoptionApplicationRepository.findById(applicationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Adoption application not found"));

        if (application.getStatus() != ApplicationStatus.UNDER_REVIEW) {
            throw new RuntimeException(
                    "Only applications under review can be approved"
            );
        }

        AdoptionAnimal animal = application.getAdoptionAnimal();

        if (animal.getStatus() != AdoptionStatus.READY_FOR_ADOPTION) {
            throw new RuntimeException(
                    "Animal is no longer available for adoption"
            );
        }

        application.setStatus(ApplicationStatus.APPROVED);

        animal.setStatus(AdoptionStatus.ADOPTED);

        adoptionAnimalRepository.save(animal);

        return adoptionApplicationRepository.save(application);
    }

    public AdoptionApplication rejectApplication(Long applicationId) {

        AdoptionApplication application =
                adoptionApplicationRepository.findById(applicationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Adoption application not found"));

        if (application.getStatus() != ApplicationStatus.UNDER_REVIEW) {
            throw new RuntimeException(
                    "Only applications under review can be rejected"
            );
        }

        application.setStatus(ApplicationStatus.REJECTED);

        return adoptionApplicationRepository.save(application);
    }
}