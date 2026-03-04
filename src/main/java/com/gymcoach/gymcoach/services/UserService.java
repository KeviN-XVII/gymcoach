package com.gymcoach.gymcoach.services;

import com.gymcoach.gymcoach.dto.UserDTO;
import com.gymcoach.gymcoach.entities.User;
import com.gymcoach.gymcoach.exceptions.BadRequestException;
import com.gymcoach.gymcoach.exceptions.NotFoundException;
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
    private final PasswordEncoder bcrypt;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder bcrypt) {
        this.userRepository = userRepository;
        this.bcrypt = bcrypt;
    }

    //SAVE UTENTE
    public User saveUser(UserDTO payload){
        //CONTROLLO EMAIL
        this.userRepository.findByEmail(payload.email()).ifPresent(user -> {
            throw new BadRequestException("L'email "+ user.getEmail() + " è già registrata!");});
        //NUOVO UTENTE
        User newUser = new User(
                payload.firstName(),
                payload.lastName(),
                payload.email(),
                bcrypt.encode(payload.password()),
                payload.role()
                );
        //SALVO UTENTE
        User savedUtente = this.userRepository.save(newUser);
        //LOG
        log.info("Utente "+newUser.getFirstName()+" "+newUser.getLastName() +" salvato con successo: ");
        return savedUtente;
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