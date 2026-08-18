package com.resq.resq.dto;

import com.resq.resq.model.AiUrgencyLevel;
import com.resq.resq.model.ReportStatus;

import java.time.LocalDateTime;

public class ReportResponseDTO {

    private Long id;

    private String animalType;

    private String description;

    private String location;

    private ReportStatus status;

    private AiUrgencyLevel urgencyLevel;

    private String imageUrl;

    private String assignedVolunteerName;

    private String rescueNote;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    public ReportResponseDTO() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getAnimalType() {
        return animalType;
    }

    public void setAnimalType(String animalType) {
        this.animalType = animalType;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }


    public AiUrgencyLevel getUrgencyLevel() {
        return urgencyLevel;
    }

    public void setUrgencyLevel(
            AiUrgencyLevel urgencyLevel
    ) {
        this.urgencyLevel = urgencyLevel;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public String getAssignedVolunteerName() {
        return assignedVolunteerName;
    }

    public void setAssignedVolunteerName(
            String assignedVolunteerName
    ) {
        this.assignedVolunteerName =
                assignedVolunteerName;
    }


    public String getRescueNote() {
        return rescueNote;
    }

    public void setRescueNote(
            String rescueNote
    ) {
        this.rescueNote =
                rescueNote;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt
    ) {
        this.createdAt = createdAt;
    }


    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(
            LocalDateTime updatedAt
    ) {
        this.updatedAt = updatedAt;
    }
}