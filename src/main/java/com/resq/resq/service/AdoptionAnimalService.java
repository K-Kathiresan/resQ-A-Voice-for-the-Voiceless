package com.resq.resq.service;

import com.resq.resq.model.AdoptionAnimal;
import com.resq.resq.model.AdoptionStatus;
import com.resq.resq.model.Report;
import com.resq.resq.model.ReportStatus;
import com.resq.resq.repository.AdoptionAnimalRepository;
import com.resq.resq.repository.ReportRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdoptionAnimalService {

    private final AdoptionAnimalRepository adoptionAnimalRepository;
    private final ReportRepository reportRepository;

    public AdoptionAnimalService(
            AdoptionAnimalRepository adoptionAnimalRepository,
            ReportRepository reportRepository) {

        this.adoptionAnimalRepository = adoptionAnimalRepository;
        this.reportRepository = reportRepository;
    }

    public AdoptionAnimal createAdoptionAnimal(
            Long reportId,
            String breed,
            String age,
            String gender,
            String healthStatus,
            String temperament,
            String description) {

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        if (report.getStatus() != ReportStatus.RESCUED) {
            throw new RuntimeException(
                    "Animal must be rescued before adding it for adoption"
            );
        }

        AdoptionAnimal animal = new AdoptionAnimal();

        animal.setReport(report);
        animal.setBreed(breed);
        animal.setAge(age);
        animal.setGender(gender);
        animal.setHealthStatus(healthStatus);
        animal.setTemperament(temperament);
        animal.setDescription(description);

        animal.setStatus(AdoptionStatus.NOT_READY);

        return adoptionAnimalRepository.save(animal);
    }

    public List<AdoptionAnimal> getAvailableAnimals() {

        return adoptionAnimalRepository
                .findByStatus(AdoptionStatus.READY_FOR_ADOPTION);
    }

    public AdoptionAnimal getAnimalById(Long id) {

        return adoptionAnimalRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Adoption animal not found"));
    }
}