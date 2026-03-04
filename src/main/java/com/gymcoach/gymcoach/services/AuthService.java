package com.gymcoach.gymcoach.services;

import com.gymcoach.gymcoach.dto.LoginDTO;
import com.gymcoach.gymcoach.entities.User;
import com.gymcoach.gymcoach.exceptions.UnauthorizedException;
import com.gymcoach.gymcoach.security.JWTTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;
    private final JWTTools jwtTools;
    private final PasswordEncoder bcrypt;

    @Autowired
    public AuthService(UserService userService, JWTTools jwtTools, PasswordEncoder bcrypt) {
        this.userService = userService;
        this.jwtTools = jwtTools;
        this.bcrypt = bcrypt;
    }

    public String CheckCredentialsAndGenerateToken(LoginDTO body) {

        //CONTROLLO CREDENZIALI
        User found = this.userService.findByEmail(body.email());

        if(bcrypt.matches(body.password(), found.getPassword())) {
         //GENERA TOKEN E LO RESTITUISCE
            String accessToken= jwtTools.generateToken(found);

            return accessToken;
        }
        else {
            throw new UnauthorizedException("Credenziali Errate");
        }
    }
}
