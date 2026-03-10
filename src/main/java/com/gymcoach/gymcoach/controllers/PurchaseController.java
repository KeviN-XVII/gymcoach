package com.gymcoach.gymcoach.controllers;

import com.gymcoach.gymcoach.dto.CheckoutResponseDTO;
import com.gymcoach.gymcoach.dto.PurchaseResponseDTO;
import com.gymcoach.gymcoach.entities.User;
import com.gymcoach.gymcoach.services.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    @Autowired
    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    // POST CREA CHECKOUT STRIPE
    @PostMapping("/checkout/{trainerProfileId}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public CheckoutResponseDTO createCheckout(@AuthenticationPrincipal User currentUser,
                                              @PathVariable UUID trainerProfileId) {
        return purchaseService.createCheckout(currentUser, trainerProfileId);
    }

    // POST WEBHOOK STRIPE
    @PostMapping("/webhook")
    public void handleWebhook(@RequestBody String payload,
                              @RequestHeader("Stripe-Signature") String sigHeader) {
        purchaseService.handleWebhook(payload, sigHeader);
    }

    // GET ACQUISTI DELL'UTENTE
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public List<PurchaseResponseDTO> getMyPurchases(@AuthenticationPrincipal User currentUser) {
        return purchaseService.findByUserId(currentUser.getId());
    }

    // GET ACQUISTI DEL TRAINER
    @GetMapping("/trainer/me")
    @PreAuthorize("hasAuthority('ROLE_TRAINER')")
    public List<PurchaseResponseDTO> getTrainerPurchases(@AuthenticationPrincipal User currentUser) {
        return purchaseService.findByTrainerProfileId(currentUser.getId());
    }
}
