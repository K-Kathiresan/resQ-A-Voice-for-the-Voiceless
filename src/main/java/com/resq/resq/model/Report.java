package com.resq.resq.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String animalType;

    private String description;

    private String location;


    @Enumerated(EnumType.STRING)
    private ReportStatus status = ReportStatus.PENDING;


    @Enumerated(EnumType.STRING)
    private AiUrgencyLevel urgencyLevel;


    private String imageUrl;


    @CreationTimestamp
    private LocalDateTime createdAt;


    @UpdateTimestamp
    private LocalDateTime updatedAt;


    @ManyToOne
    @JoinColumn(name = "volunteer_id")
    private Volunteer volunteer;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    @ManyToOne
    @JoinColumn(name = "assigned_volunteer_id")
    private User assignedVolunteer;


    private String rescueNote;


    public Report() {
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


    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(
            Volunteer volunteer
    ) {
        this.volunteer = volunteer;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public User getAssignedVolunteer() {
        return assignedVolunteer;
    }

    public void setAssignedVolunteer(
            User assignedVolunteer
    ) {
        this.assignedVolunteer = assignedVolunteer;
    }


    public String getRescueNote() {
        return rescueNote;
    }

    public void setRescueNote(
            String rescueNote
    ) {
        this.rescueNote = rescueNote;
    }
}