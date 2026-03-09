package com.gymcoach.gymcoach.services;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcoach.gymcoach.config.OpenRouterConfig;
import com.gymcoach.gymcoach.dto.ExerciseResponseDTO;
import com.gymcoach.gymcoach.dto.WorkoutDayResponseDTO;
import com.gymcoach.gymcoach.dto.WorkoutPlanResponseDTO;
import com.gymcoach.gymcoach.entities.*;
import com.gymcoach.gymcoach.exceptions.NotFoundException;
import com.gymcoach.gymcoach.repositories.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@Slf4j
public class AIService {

    private final OpenRouterConfig openRouterConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserProfileRepository userProfileRepository;
    private final WorkoutPlanRepository workoutPlanRepository;
    private final WorkoutDayRepository workoutDayRepository;
    private final ExerciseRepository exerciseRepository;

    @Autowired
    public AIService(OpenRouterConfig openRouterConfig,
                     RestTemplate restTemplate,
                     UserProfileRepository userProfileRepository,
                     WorkoutPlanRepository workoutPlanRepository,
                     WorkoutDayRepository workoutDayRepository,
                     ExerciseRepository exerciseRepository) {
        this.openRouterConfig = openRouterConfig;
        this.restTemplate = restTemplate;
        this.userProfileRepository = userProfileRepository;
        this.workoutPlanRepository = workoutPlanRepository;
        this.workoutDayRepository = workoutDayRepository;
        this.exerciseRepository = exerciseRepository;
    }

    // GENERO PIANO
    public WorkoutPlanResponseDTO generatePlan(User currentUser) {
        UserProfile profile = userProfileRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new NotFoundException("Profilo utente non trovato! Completa il profilo prima di generare una scheda."));

        String prompt = buildPrompt(profile);
        String jsonResponse = callOpenRouter(prompt);
        return parseAndSave(jsonResponse, currentUser);
    }

    // STEP 1 — COSTRUISCE IL PROMPT
    private String buildPrompt(UserProfile profile) {
        return """
                Sei un personal trainer esperto. Genera una scheda di allenamento personalizzata in formato JSON.
                
                Dati dell'utente:
                - Età: %d anni
                - Genere: %s
                - Peso: %.1f kg
                - Altezza: %.1f cm
                - Obiettivo: %s
                - Livello: %s
                - Frequenza settimanale: %d giorni a settimana
                
                Rispondi SOLO con un JSON valido, senza testo aggiuntivo, senza ```json, senza ```, nel seguente formato:
                {
                  "title": "Nome della scheda",
                  "description": "Descrizione della scheda",
                  "goal": "%s",
                  "level": "%s",
                  "durationWeeks": 8,
                  "workoutDays": [
                    {
                      "dayNumber": 1,
                      "dayName": "es. Lunedì - Petto e Tricipiti",
                      "notes": "Note opzionali",
                      "exercises": [
                        {
                          "name": "Nome esercizio",
                          "sets": 4,
                          "reps": 10,
                          "restSeconds": 90,
                          "notes": "Note tecnica esecuzione",
                          "orderIndex": 1
                        }
                      ]
                    }
                  ]
                }
                
                IMPORTANTE:
                - Il campo "goal" deve essere ESATTAMENTE uno di questi valori: MASSA, DIMAGRIMENTO, TONIFICAZIONE
                - Il campo "level" deve essere ESATTAMENTE uno di questi valori: PRINCIPIANTE, INTERMEDIO, AVANZATO
                - Genera esattamente %d giorni di allenamento, ognuno con 4-6 esercizi adatti all'obiettivo e al livello
                - Il campo "reps" deve essere SEMPRE un numero intero, mai testo come "MAX" o "10 per gamba"
                - Se l'esercizio prevede massimo numero di ripetizioni, usa 0
                - Se l'esercizio è per singola gamba/braccio, scrivi le reps per lato come numero
                - Non aggiungere testo prima o dopo il JSON
                """.formatted(
                profile.getAge(),
                profile.getGender(),
                profile.getWeightKg(),
                profile.getHeightCm(),
                profile.getGoal(),
                profile.getLevel(),
                profile.getWeeklyFrequency(),
                profile.getGoal(),
                profile.getLevel(),
                profile.getWeeklyFrequency()
        );
    }

    // STEP 2 — CHIAMA OPENROUTER
    private String callOpenRouter(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openRouterConfig.getApiKey());

        Map<String, Object> message = Map.of(
                "role", "user",
                "content", prompt
        );

        Map<String, Object> body = Map.of(
                "model", openRouterConfig.getModel(),
                "messages", List.of(message)
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                openRouterConfig.getApiUrl(), request, String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Errore OpenRouter: " + response.getStatusCode());
        }

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode choices = root.path("choices");
            if (choices.isEmpty()) {
                throw new RuntimeException("Risposta OpenRouter senza choices");
            }
            return choices.get(0).path("message").path("content").asText();
        } catch (Exception e) {
            throw new RuntimeException("Errore parsing risposta OpenRouter", e);
        }
    }

    // STEP 3 — PARSA IL JSON E SALVA NEL DB
    private WorkoutPlanResponseDTO parseAndSave(String jsonResponse, User currentUser) {
        try {
            // PULIZIA — a volte l'AI aggiunge ```json nonostante il prompt
            String cleanJson = jsonResponse
                    .replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();

            JsonNode root = objectMapper.readTree(cleanJson);

            // SALVO WORKOUT PLAN
            WorkoutPlan plan = new WorkoutPlan(
                    root.path("title").asText(),
                    root.path("description").asText(),
                    Goal.valueOf(root.path("goal").asText().toUpperCase().trim()),
                    Level.valueOf(root.path("level").asText().toUpperCase().trim()),
                    root.path("durationWeeks").asInt(),
                    0,
                    true,
                    null,
                    currentUser
            );
            WorkoutPlan savedPlan = workoutPlanRepository.save(plan);

            // SALVO I GIORNI + ESERCIZI
            List<WorkoutDayResponseDTO> daysDTO = new ArrayList<>();

            for (JsonNode dayNode : root.path("workoutDays")) {
                WorkoutDay day = new WorkoutDay(
                        savedPlan,
                        dayNode.path("dayNumber").asInt(),
                        dayNode.path("dayName").asText(),
                        dayNode.path("notes").asText()
                );
                WorkoutDay savedDay = workoutDayRepository.save(day);

                List<ExerciseResponseDTO> exercisesDTO = new ArrayList<>();

                for (JsonNode exNode : dayNode.path("exercises")) {
                    Exercise exercise = new Exercise(
                            savedDay,
                            exNode.path("name").asText(),
                            exNode.path("sets").asInt(),
                            exNode.path("reps").asInt(),
                            exNode.path("restSeconds").asInt(),
                            exNode.path("notes").asText(),
                            exNode.path("orderIndex").asInt()
                    );
                    Exercise savedExercise = exerciseRepository.save(exercise);

                    exercisesDTO.add(new ExerciseResponseDTO(
                            savedExercise.getId(),
                            savedExercise.getName(),
                            savedExercise.getSets(),
                            savedExercise.getReps(),
                            savedExercise.getRestSeconds(),
                            savedExercise.getNotes(),
                            savedExercise.getOrderIndex()
                    ));
                }

                daysDTO.add(new WorkoutDayResponseDTO(
                        savedDay.getId(),
                        savedDay.getDayNumber(),
                        savedDay.getDayName(),
                        savedDay.getNotes(),
                        exercisesDTO
                ));
            }

            log.info("Scheda AI generata e salvata per l'utente {}", currentUser.getFirstName());

            return new WorkoutPlanResponseDTO(
                    savedPlan.getId(),
                    savedPlan.getTitle(),
                    savedPlan.getDescription(),
                    savedPlan.getGoal(),
                    savedPlan.getLevel(),
                    savedPlan.getDurationWeeks(),
                    savedPlan.getPrice(),
                    savedPlan.isAiGenerated(),
                    daysDTO
            );

        } catch (Exception e) {
            throw new RuntimeException("Errore nel parsing del JSON generato dall'AI: " + e.getMessage());
        }
    }
}
