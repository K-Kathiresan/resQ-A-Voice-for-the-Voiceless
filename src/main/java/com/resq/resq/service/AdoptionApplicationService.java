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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    /*
     * ============================================================
     * APPLY FOR ADOPTION
     * ============================================================
     */
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

        // Animal must be available
        if (animal.getStatus() != AdoptionStatus.READY_FOR_ADOPTION) {
            throw new RuntimeException(
                    "This animal is not currently available for adoption"
            );
        }

        User applicant = userRepository.findById(applicantId)
                .orElseThrow(() ->
                        new RuntimeException("Applicant not found"));

        // Prevent duplicate application from same user for same animal
        if (adoptionApplicationRepository
                .existsByAdoptionAnimalIdAndApplicantId(
                        animalId,
                        applicantId)) {

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


    /*
     * ============================================================
     * REVIEW APPLICATION
     * ============================================================
     */
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

        // Animal must still be available
        AdoptionAnimal animal = application.getAdoptionAnimal();

        if (animal.getStatus() != AdoptionStatus.READY_FOR_ADOPTION) {

            throw new RuntimeException(
                    "Animal is no longer available for adoption"
            );
        }

        application.setStatus(ApplicationStatus.UNDER_REVIEW);

        return adoptionApplicationRepository.save(application);
    }


    /*
     * ============================================================
     * APPROVE APPLICATION
     * ============================================================
     *
     * IMPORTANT:
     * One animal can have only ONE approved application.
     *
     * Once approved:
     *
     * Animal → ADOPTED
     * Selected application → APPROVED
     * Other active applications → REJECTED
     *
     * Transaction ensures the complete operation succeeds/fails
     * as one unit.
     */
    @Transactional
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

        Long animalId = application.getAdoptionAnimal().getId();

        /*
        * Lock the animal row while this approval transaction
        * is being processed.
        *
        * This prevents two admins from approving different
        * applications for the same animal simultaneously.
        */
        AdoptionAnimal animal =
                adoptionAnimalRepository.findByIdForUpdate(animalId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Adoption animal not found"
                                ));

        if (animal.getStatus() != AdoptionStatus.READY_FOR_ADOPTION) {
        throw new RuntimeException(
                "Animal is no longer available for adoption"
        );
        }

        /*
         * Approve selected application
         */
        application.setStatus(ApplicationStatus.APPROVED);

        /*
         * Mark animal as adopted
         */
        animal.setStatus(AdoptionStatus.ADOPTED);

        adoptionAnimalRepository.save(animal);

        /*
         * Reject all other applications for this animal.
         *
         * We deliberately don't touch the selected application.
         */
        List<AdoptionApplication> applications =
                adoptionApplicationRepository
                        .findByAdoptionAnimal(animal);

        for (AdoptionApplication otherApplication : applications) {

            if (!otherApplication.getId()
                    .equals(application.getId())
                    &&
                    (otherApplication.getStatus()
                            == ApplicationStatus.SUBMITTED
                    ||
                    otherApplication.getStatus()
                            == ApplicationStatus.UNDER_REVIEW)) {

                otherApplication.setStatus(
                        ApplicationStatus.REJECTED
                );
            }
        }

        adoptionApplicationRepository.saveAll(applications);

        return adoptionApplicationRepository.save(application);
    }


    /*
     * ============================================================
     * REJECT APPLICATION
     * ============================================================
     */
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


    /*
     * ============================================================
     * GET USER APPLICATIONS
     * ============================================================
     */
    public List<AdoptionApplication> getApplicationsByUser(
            Long applicantId) {

        User applicant = userRepository.findById(applicantId)
                .orElseThrow(() ->
                        new RuntimeException("Applicant not found"));

        return adoptionApplicationRepository
                .findByApplicant(applicant);
    }


    /*
     * ============================================================
     * GET ALL APPLICATIONS
     * ============================================================
     */
    public List<AdoptionApplication> getAllApplications() {

        return adoptionApplicationRepository.findAll();
    }
}