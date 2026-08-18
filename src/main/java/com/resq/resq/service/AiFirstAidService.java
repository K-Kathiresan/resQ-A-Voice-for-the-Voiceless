package com.resq.resq.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import com.google.genai.types.Schema;
import com.google.genai.types.Type;
import com.resq.resq.dto.AiFirstAidResponseDTO;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class AiFirstAidService {

    private final Client client;
    private final ObjectMapper objectMapper;


    public AiFirstAidService() {
        this.client = new Client();
        this.objectMapper = new ObjectMapper();
    }


    public String testGeminiConnection() {

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash",
                        "Reply with exactly: resQ AI connection successful",
                        null
                );

        return response.text();
    }


    public AiFirstAidResponseDTO analyzeAnimalImage(
            MultipartFile image,
            String animalType,
            String description
    ) throws IOException {

        // Schema for all List<String> fields
        Schema stringArraySchema = Schema.builder()
                .type(Type.Known.ARRAY)
                .items(
                        Schema.builder()
                                .type(Type.Known.STRING)
                                .build()
                )
                .build();


        // Exact JSON structure expected from Gemini
        Schema responseSchema = Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(
                        Map.of(

                                "urgencyLevel",
                                Schema.builder()
                                        .type(Type.Known.STRING)
                                        .enum_(
                                                List.of(
                                                        "LOW",
                                                        "MEDIUM",
                                                        "HIGH",
                                                        "CRITICAL"
                                                )
                                        )
                                        .build(),

                                // Main actions shown directly to the Citizen
                                "mainFirstAidActions",
                                stringArraySchema,

                                // Additional detailed information
                                "visibleConcerns",
                                stringArraySchema,

                                "immediateActions",
                                stringArraySchema,

                                "precautions",
                                stringArraySchema,

                                "doNotDo",
                                stringArraySchema,

                                "requiresUrgentHelp",
                                Schema.builder()
                                        .type(Type.Known.BOOLEAN)
                                        .build(),

                                "disclaimer",
                                Schema.builder()
                                        .type(Type.Known.STRING)
                                        .build()
                        )
                )
                .required(
                        List.of(
                                "urgencyLevel",
                                "mainFirstAidActions",
                                "visibleConcerns",
                                "immediateActions",
                                "precautions",
                                "doNotDo",
                                "requiresUrgentHelp",
                                "disclaimer"
                        )
                )
                .build();


        // Force Gemini to return structured JSON
        GenerateContentConfig config =
                GenerateContentConfig.builder()
                        .responseMimeType("application/json")
                        .responseSchema(responseSchema)
                        .build();


        String prompt = """
                You are an AI assistant for the resQ animal rescue platform.

                Analyze the uploaded animal image together with the user's description.

                Animal type: %s
                User description: %s

                Your role is to provide preliminary, safety-conscious guidance
                before trained rescue help or veterinary help arrives.

                STRICT SAFETY RULES:

                - Describe only concerns reasonably supported by the image
                and the user's description.
                - Do not make a veterinary diagnosis or claim certainty.
                - If something cannot be determined, do not present it as fact.
                - Do not recommend human medication.
                - Do not recommend invasive treatment or medical procedures.
                - Do not recommend feeding the animal as part of preliminary
                emergency guidance.
                - Do not recommend giving food or water unless there is a clear,
                low-risk reason supported by the provided information.
                - Never tell the user to force-feed or force water.
                - Prioritize user safety around injured, frightened,
                or aggressive animals.
                - Keep all suggested actions conservative and low-risk.

                URGENCY LEVELS:

                LOW = no obvious severe distress visible.
                MEDIUM = possible concern requiring rescue attention.
                HIGH = serious concern requiring urgent intervention.
                CRITICAL = obvious potentially life-threatening condition
                requiring immediate emergency intervention.

                MAIN FIRST-AID ACTIONS:

                - mainFirstAidActions is the most important field.
                - Provide exactly 3 to 5 short, clear, prioritized actions.
                - These actions will be shown directly to a stressed citizen.
                - Each action must be simple, practical, and easy to understand.
                - Include only safe, low-risk actions.
                - Do not recommend feeding the animal.
                - Do not recommend giving food or water unless clearly safe
                and necessary based on the provided information.
                - Prioritize what the user should do RIGHT NOW.

                OTHER FIELDS:

                - visibleConcerns: concise observations relevant to rescuers.
                - immediateActions: additional safe actions if needed.
                - precautions: important safety precautions.
                - doNotDo: unsafe actions the user must avoid.

                Keep all lists concise. Avoid repeating the same advice
                across multiple fields.

                Return strictly according to the provided JSON schema.
                """
                .formatted(
                        animalType == null || animalType.isBlank()
                                ? "Unknown"
                                : animalType,

                        description == null || description.isBlank()
                                ? "No description provided"
                                : description
                );


        // Combine text prompt + uploaded animal image
        Content content = Content.fromParts(
                Part.fromText(prompt),

                Part.fromBytes(
                        image.getBytes(),
                        image.getContentType()
                )
        );


        // Send image analysis request to Gemini
        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash",
                        content,
                        config
                );


        // Convert Gemini JSON response into our DTO
        return objectMapper.readValue(
                response.text(),
                AiFirstAidResponseDTO.class
        );
    }
}