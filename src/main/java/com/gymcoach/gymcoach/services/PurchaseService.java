package com.gymcoach.gymcoach.services;

import com.gymcoach.gymcoach.config.StripeConfig;
import com.gymcoach.gymcoach.dto.CheckoutResponseDTO;
import com.gymcoach.gymcoach.dto.PurchaseResponseDTO;
import com.gymcoach.gymcoach.entities.*;
import com.gymcoach.gymcoach.exceptions.NotFoundException;
import com.gymcoach.gymcoach.exceptions.UnauthorizedException;
import com.gymcoach.gymcoach.repositories.PurchaseRepository;
import com.gymcoach.gymcoach.repositories.TrainerProfileRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final TrainerProfileRepository trainerProfileRepository;
    private final UserService userService;
    private final StripeConfig stripeConfig;

    @Value("${frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Autowired
    public PurchaseService(PurchaseRepository purchaseRepository,
                           TrainerProfileRepository trainerProfileRepository,
                           UserService userService,
                           StripeConfig stripeConfig) {
        this.purchaseRepository = purchaseRepository;
        this.trainerProfileRepository = trainerProfileRepository;
        this.userService = userService;
        this.stripeConfig = stripeConfig;
    }

    // CONVERSIONE
    private PurchaseResponseDTO toResponseDTO(Purchase p) {
        return new PurchaseResponseDTO(
                p.getId(),
                p.getUser().getId(),
                p.getUser().getFirstName() + " " + p.getUser().getLastName(),
                p.getTrainerProfile().getId(),
                p.getTrainerProfile().getUser().getFirstName() + " " + p.getTrainerProfile().getUser().getLastName(),
                p.getAmount(),
                p.getStatus(),
                p.getCreatedAt()
        );
    }

    // POST CREA SESSIONE CHECKOUT STRIPE
    public CheckoutResponseDTO createCheckout(User currentUser, UUID trainerProfileId) {
        TrainerProfile trainerProfile = trainerProfileRepository.findById(trainerProfileId)
                .orElseThrow(() -> new NotFoundException("Profilo trainer non trovato!"));

        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(frontendUrl + "/payment/success?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(frontendUrl + "/payment/cancel")
                    .addLineItem(SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("eur")
                                    .setUnitAmount((long) (trainerProfile.getPricePlan() * 100)) // Stripe vuole i centesimi
                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName("Scheda di allenamento - " + trainerProfile.getUser().getFirstName() + " " + trainerProfile.getUser().getLastName())
                                            .setDescription("Scheda personalizzata creata dal trainer")
                                            .build())
                                    .build())
                            .build())
                    .putMetadata("userId", currentUser.getId().toString())
                    .putMetadata("trainerProfileId", trainerProfileId.toString())
                    .build();

            Session session = Session.create(params);

            // SALVO L'ACQUISTO CON STATUS PENDING
            Purchase purchase = new Purchase(
                    currentUser,
                    trainerProfile,
                    session.getId(),
                    trainerProfile.getPricePlan()
            );
            purchaseRepository.save(purchase);

            log.info("Sessione Stripe creata per utente {} con trainer {}", currentUser.getFirstName(), trainerProfile.getUser().getFirstName());
            return new CheckoutResponseDTO(session.getUrl(), session.getId());

        } catch (Exception e) {
            throw new RuntimeException("Errore nella creazione della sessione Stripe: " + e.getMessage());
        }
    }

    // POST WEBHOOK STRIPE — chiamato da Stripe quando il pagamento va a buon fine
    public void handleWebhook(String payload, String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, stripeConfig.getWebhookSecret());
        } catch (SignatureVerificationException e) {
            throw new UnauthorizedException("Firma webhook non valida!");
        }

        if ("checkout.session.completed".equals(event.getType())) {

            Session session = (Session) event
                    .getData()
                    .getObject();

            if (session == null) {
                throw new RuntimeException("Session Stripe null");
            }

            Purchase purchase = purchaseRepository
                    .findByStripeSessionId(session.getId())
                    .orElseThrow(() -> new NotFoundException(
                            "Acquisto non trovato per la sessione: " + session.getId()
                    ));

            purchase.setStatus(PurchaseStatus.COMPLETED);
            purchase.setStripePaymentIntent(session.getPaymentIntent());
            purchaseRepository.save(purchase);

            log.info("Pagamento completato per acquisto {}", purchase.getId());
        }
    }

    // GET ACQUISTI DELL'UTENTE
    public List<PurchaseResponseDTO> findByUserId(UUID userId) {
        return purchaseRepository.findByUserId(userId)
                .stream().map(this::toResponseDTO).toList();
    }

    // GET ACQUISTI DEL TRAINER
    public List<PurchaseResponseDTO> findByTrainerProfileId(UUID userId) {
        TrainerProfile trainerProfile = trainerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Profilo trainer non trovato!"));
        return purchaseRepository.findByTrainerProfileId(trainerProfile.getId())
                .stream().map(this::toResponseDTO).toList();
    }
}
