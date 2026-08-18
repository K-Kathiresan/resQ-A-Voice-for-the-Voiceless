package com.resq.resq.controller;

import com.resq.resq.dto.AiFirstAidResponseDTO;
import com.resq.resq.payload.ApiResponse;
import com.resq.resq.service.AiFirstAidService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/ai")
public class AiFirstAidController {

    private final AiFirstAidService aiFirstAidService;


    public AiFirstAidController(
            AiFirstAidService aiFirstAidService
    ) {
        this.aiFirstAidService = aiFirstAidService;
    }


    // TEMPORARY CONNECTION TEST
    @GetMapping("/test")
    public ResponseEntity<ApiResponse<String>> testGeminiConnection() {

        String result =
                aiFirstAidService.testGeminiConnection();

        ApiResponse<String> response =
                new ApiResponse<>(
                        true,
                        "Gemini connection successful",
                        result
                );

        return ResponseEntity.ok(response);
    }


    // REAL AI FIRST-AID ANALYSIS ENDPOINT
    @PostMapping("/first-aid")
    public ResponseEntity<ApiResponse<AiFirstAidResponseDTO>> analyzeFirstAid(
            @RequestParam("image") MultipartFile image,

            @RequestParam(
                    value = "animalType",
                    required = false
            )
            String animalType,

            @RequestParam(
                    value = "description",
                    required = false
            )
            String description
    ) throws IOException {

        AiFirstAidResponseDTO result =
                aiFirstAidService.analyzeAnimalImage(
                        image,
                        animalType,
                        description
                );

        ApiResponse<AiFirstAidResponseDTO> response =
                new ApiResponse<>(
                        true,
                        "AI first-aid guidance generated successfully",
                        result
                );

        return ResponseEntity.ok(response);
    }
}