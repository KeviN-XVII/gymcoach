package com.gymcoach.gymcoach.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.gymcoach.gymcoach.dto.*;
import com.gymcoach.gymcoach.entities.*;
import com.gymcoach.gymcoach.exceptions.BadRequestException;
import com.gymcoach.gymcoach.exceptions.NotFoundException;
import com.gymcoach.gymcoach.repositories.TrainerProfileRepository;
import com.gymcoach.gymcoach.repositories.UserProfileRepository;
import com.gymcoach.gymcoach.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final TrainerProfileRepository trainerProfileRepository;
    private final PasswordEncoder bcrypt;
    private final Cloudinary cloudinaryUploader;

    @Autowired
    public UserService(UserRepository userRepository,
                       UserProfileRepository userProfileRepository,
                       TrainerProfileRepository trainerProfileRepository,
                       PasswordEncoder bcrypt, Cloudinary cloudinaryUploader) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.trainerProfileRepository = trainerProfileRepository;
        this.bcrypt = bcrypt;
        this.cloudinaryUploader = cloudinaryUploader;
    }

    // CONVERSIONE
    private UserResponseDTO toResponseDTO(User u) {
        return new UserResponseDTO(
                u.getId(), u.getFirstName(), u.getLastName(),
                u.getEmail(), u.getRole(), u.getAvatar()
        );
    }

    // REGISTRAZIONE UTENTE NORMALE
    public UserResponseDTO saveUser(RegisterUserDTO payload) {
        this.userRepository.findByEmail(payload.email()).ifPresent(user -> {
            throw new BadRequestException("L'email " + user.getEmail() + " è già registrata!");
        });

        User newUser = new User(
                payload.firstName(), payload.lastName(),
                payload.email(), bcrypt.encode(payload.password()), Role.USER
        );
        newUser.setAvatar("https://ui-avatars.com/api/?name=" + payload.firstName() + "+" + payload.lastName());
        User savedUser = this.userRepository.save(newUser);

        UserProfile userProfile = new UserProfile(
                savedUser, payload.weightKg(), payload.heightCm(),
                payload.gender(), payload.age(), payload.goal(),
                payload.level(), payload.weeklyFrequency()
        );
        this.userProfileRepository.save(userProfile);

        log.info("Utente {} {} salvato con successo", savedUser.getFirstName(), savedUser.getLastName());
        return toResponseDTO(savedUser);
    }

    // REGISTRAZIONE PERSONAL TRAINER
    public UserResponseDTO saveTrainer(RegisterTrainerDTO payload) {
        this.userRepository.findByEmail(payload.email()).ifPresent(user -> {
            throw new BadRequestException("L'email " + user.getEmail() + " è già registrata!");
        });

        User newUser = new User(
                payload.firstName(), payload.lastName(),
                payload.email(), bcrypt.encode(payload.password()), Role.TRAINER
        );
        newUser.setAvatar("https://ui-avatars.com/api/?name=" + payload.firstName() + "+" + payload.lastName());
        User savedUser = this.userRepository.save(newUser);

        TrainerProfile trainerProfile = new TrainerProfile(
                savedUser, payload.bio(), payload.specialization(),
                payload.certifications(), payload.pricePlan()
        );
        this.trainerProfileRepository.save(trainerProfile);

        log.info("Trainer {} {} salvato con successo", savedUser.getFirstName(), savedUser.getLastName());
        return toResponseDTO(savedUser);
    }

    // AGGIORNA UTENTE
    public UserResponseDTO updateUser(UUID userId, UserDTO payload) {
        User user = this.findById(userId);
        if (payload.firstName() != null) user.setFirstName(payload.firstName());
        if (payload.lastName() != null) user.setLastName(payload.lastName());
        if (payload.email() != null) {
            this.userRepository.findByEmail(payload.email()).ifPresent(u -> {
                if (!u.getId().equals(userId))
                    throw new BadRequestException("L'email " + payload.email() + " è già in uso!");
            });
            user.setEmail(payload.email());
        }
        if (payload.password() != null) user.setPassword(bcrypt.encode(payload.password()));

        User updatedUser = this.userRepository.save(user);
        log.info("Utente {} {} aggiornato con successo", updatedUser.getFirstName(), updatedUser.getLastName());
        return toResponseDTO(updatedUser);
    }

    // UPLOAD AVATAR
    public UserResponseDTO uploadAvatar(UUID userId, MultipartFile file) {
        User found = this.findById(userId);
        try {
            Map result = cloudinaryUploader.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            found.setAvatar((String) result.get("secure_url"));
            return toResponseDTO(userRepository.save(found));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // METODI
    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utente con id " + id + " non trovato!"));
    }

    public UserResponseDTO findByIdAsDTO(UUID id) {
        return toResponseDTO(this.findById(id));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Utente con email " + email + " non trovato!"));
    }
}