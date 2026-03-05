package com.gymcoach.gymcoach.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.gymcoach.gymcoach.dto.RegisterTrainerDTO;
import com.gymcoach.gymcoach.dto.RegisterUserDTO;
import com.gymcoach.gymcoach.dto.UserDTO;
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
                       PasswordEncoder bcrypt,Cloudinary cloudinaryUploader) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.trainerProfileRepository = trainerProfileRepository;
        this.bcrypt = bcrypt;
        this.cloudinaryUploader = cloudinaryUploader;
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

    // AGGIORNA UTENTE
    public User updateUser(UUID userId, UserDTO payload) {
        User user = this.findById(userId);
        // AGGIORNA SOLO I CAMPI NON NULL
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
        log.info("Utente " + updatedUser.getFirstName() + " " + updatedUser.getLastName() + " aggiornato con successo");
        return updatedUser;
    }


    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utente con id " + id + " non trovato!"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Utente con email " + email + " non trovato!"));
    }

    //UPLOAD AVATAR
    public User uploadAvatar(UUID userId, MultipartFile file){
        User found = this.findById(userId);
        try {
            Map result = cloudinaryUploader.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

            String imageUrl = (String) result.get("secure_url");

            found.setAvatar(imageUrl);


            return userRepository.save(found);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

}