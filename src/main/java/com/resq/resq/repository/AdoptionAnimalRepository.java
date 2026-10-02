package com.resq.resq.repository;

import com.resq.resq.model.AdoptionAnimal;
import com.resq.resq.model.AdoptionStatus;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AdoptionAnimalRepository
        extends JpaRepository<AdoptionAnimal, Long> {

    List<AdoptionAnimal> findByStatus(
            AdoptionStatus status
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM AdoptionAnimal a WHERE a.id = :id")
    Optional<AdoptionAnimal> findByIdForUpdate(
            @Param("id") Long id
    );
}