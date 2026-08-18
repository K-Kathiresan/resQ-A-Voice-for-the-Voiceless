package com.resq.resq.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "adoption_applications")
public class AdoptionApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "adoption_animal_id", nullable = false)
    private AdoptionAnimal adoptionAnimal;

    @ManyToOne
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;

    private String housingType;

    private String animalExperience;

    @Column(length = 1000)
    private String reason;

    private String otherPets;

    private String contactPreference;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.SUBMITTED;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public AdoptionApplication() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AdoptionAnimal getAdoptionAnimal() {
        return adoptionAnimal;
    }

    public void setAdoptionAnimal(AdoptionAnimal adoptionAnimal) {
        this.adoptionAnimal = adoptionAnimal;
    }

    public User getApplicant() {
        return applicant;
    }

    public void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    public String getHousingType() {
        return housingType;
    }

    public void setHousingType(String housingType) {
        this.housingType = housingType;
    }

    public String getAnimalExperience() {
        return animalExperience;
    }

    public void setAnimalExperience(String animalExperience) {
        this.animalExperience = animalExperience;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getOtherPets() {
        return otherPets;
    }

    public void setOtherPets(String otherPets) {
        this.otherPets = otherPets;
    }

    public String getContactPreference() {
        return contactPreference;
    }

    public void setContactPreference(String contactPreference) {
        this.contactPreference = contactPreference;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}