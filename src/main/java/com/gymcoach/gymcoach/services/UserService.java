package com.gymcoach.gymcoach.services;

import com.gymcoach.gymcoach.dto.RegisterTrainerDTO;
import com.gymcoach.gymcoach.dto.RegisterUserDTO;
import com.gymcoach.gymcoach.entities.Role;
import com.gymcoach.gymcoach.entities.TrainerProfile;
import com.gymcoach.gymcoach.entities.User;
import com.gymcoach.gymcoach.entities.UserProfile;
import com.gymcoach.gymcoach.exceptions.BadRequestException;
import com.gymcoach.gymcoach.exceptions.NotFoundException;
import com.gymcoach.gymcoach.repositories.TrainerProfileRepository;
import com.gymcoach.gymcoach.repositories.UserProfileRepository;
import com.gymcoach.gymcoach.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final TrainerProfileRepository trainerProfileRepository;
    private final PasswordEncoder bcrypt;

    @Autowired
    public UserService(UserRepository userRepository,
                       UserProfileRepository userProfileRepository,
                       TrainerProfileRepository trainerProfileRepository,
                       PasswordEncoder bcrypt) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.trainerProfileRepository = trainerProfileRepository;
        this.bcrypt = bcrypt;
    }

    // REGISTRAZIONE UTENTE NORMALE
    public User saveUser(RegisterUserDTO payload) {
        // CONTROLLO EMAIL
        this.userRepository.findByEmail(payload.email()).ifPresent(user -> {
            throw new BadRequestException("L'email " + user.getEmail() + " è già registrata!");
        });

        // CREO UTENTE
        User newUser = new User(
                payload.firstName(),
                payload.lastName(),
                payload.email(),
                bcrypt.encode(payload.password()),
                Role.USER
        );

        // SETTO AVATAR DI DEFAULT
        newUser.setAvatar("https://ui-avatars.com/api/?name=" + payload.firstName() + "+" + payload.lastName());

        // SALVO UTENTE
        User savedUser = this.userRepository.save(newUser);

        // CREO PROFILO FISICO
        UserProfile userProfile = new UserProfile(
                savedUser,
                payload.weightKg(),
                payload.heightCm(),
                payload.gender(),
                payload.age(),
                payload.goal(),
                payload.level(),
                payload.weeklyFrequency()
        );

        // SALVO PROFILO FISICO
        this.userProfileRepository.save(userProfile);

        // LOG
        log.info("Utente " + savedUser.getFirstName() + " " + savedUser.getLastName() + " salvato con successo");
        return savedUser;
    }

    // REGISTRAZIONE PERSONAL TRAINER
    public User saveTrainer(RegisterTrainerDTO payload) {
        // CONTROLLO EMAIL
        this.userRepository.findByEmail(payload.email()).ifPresent(user -> {
            throw new BadRequestException("L'email " + user.getEmail() + " è già registrata!");
        });

        // CREO UTENTE
        User newUser = new User(
                payload.firstName(),
                payload.lastName(),
                payload.email(),
                bcrypt.encode(payload.password()),
                Role.TRAINER
        );

        // SETTO AVATAR DI DEFAULT
        newUser.setAvatar("https://ui-avatars.com/api/?name=" + payload.firstName() + "+" + payload.lastName());

        // SALVO UTENTE
        User savedUser = this.userRepository.save(newUser);

        // CREO PROFILO TRAINER
        TrainerProfile trainerProfile = new TrainerProfile(
                savedUser,
                payload.bio(),
                payload.specialization(),
                payload.certifications(),
                payload.pricePlan()
        );

        // SALVO PROFILO TRAINER
        this.trainerProfileRepository.save(trainerProfile);

        // LOG
        log.info("Trainer " + savedUser.getFirstName() + " " + savedUser.getLastName() + " salvato con successo");
        return savedUser;
    }


    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utente con id " + id + " non trovato!"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Utente con email " + email + " non trovato!"));
    }
}