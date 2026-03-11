package com.gymcoach.gymcoach.security;

import com.gymcoach.gymcoach.entities.User;
import com.gymcoach.gymcoach.exceptions.UnauthorizedException;
import com.gymcoach.gymcoach.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTCheckedFilter extends OncePerRequestFilter {

    private final JWTTools jwtTools;
    private final UserService userService;

    @Autowired
    public JWTCheckedFilter(JWTTools jwtTools, UserService userService) {
        this.jwtTools = jwtTools;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }
//            throw new UnauthorizedException("Inserire il token nell'Authorization header nel formato corretto!");


        String accessToken = authHeader.replace("Bearer ", "");
        jwtTools.verifyToken(accessToken);

        String email = jwtTools.extractEmailFromToken(accessToken);
        User authenticatedUser = userService.findByEmail(email);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                authenticatedUser,
                null,
                authenticatedUser.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        AntPathMatcher matcher = new AntPathMatcher();
        return matcher.match("/auth/**", request.getServletPath()) ||
                matcher.match("/purchases/webhook", request.getServletPath());
    }
}
