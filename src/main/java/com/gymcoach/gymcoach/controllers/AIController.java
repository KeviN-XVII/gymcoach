package com.gymcoach.gymcoach.controllers;

import com.gymcoach.gymcoach.dto.WorkoutPlanResponseDTO;
import com.gymcoach.gymcoach.entities.User;
import com.gymcoach.gymcoach.services.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class AIController {

    private final AIService aiService;

    @Autowired
    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public WorkoutPlanResponseDTO generatePlan(@AuthenticationPrincipal User currentUser) {
        return aiService.generatePlan(currentUser);
    }
}
