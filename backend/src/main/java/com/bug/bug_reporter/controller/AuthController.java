package com.bug.bug_reporter.controller;

import com.bug.bug_reporter.dto.LoginRequest;
import com.bug.bug_reporter.dto.LoginResponse;
import com.bug.bug_reporter.security.CustomUserDetails;
import com.bug.bug_reporter.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// This file had conflict when trying to merge into main
// because Max and Cezar worked on searatte branches at the same time
// I kept the functionality from both because they were'nt contracdicting each other logically
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService; // From Cezar

    @PostMapping("/login")
    public ResponseEntity<?> login( // ResponseEntity<?> from Max to allow returning Map or LoginResponse
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request
    ) {
        try { // try-catch block from Max
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());

            Authentication auth = authenticationManager.authenticate(authToken);

            SecurityContextHolder.getContext().setAuthentication(auth);

            request.getSession(true).setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext()
            );

            // Cast to CustomUserDetails
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

            // Extract the required fields
            Long id = userDetails.getId();
            String username = userDetails.getUsername();

            // Extract the role from Authorities
            String role = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(r -> r.replace("ROLE_", "")) // Strips the prefix for the response
                    .findFirst()
                    .orElse("USER"); // Fallback, though CustomUserDetails guarantees a role

            // Score calculation and updated response from Cezar
            double score = userService.getUserScore(id).getScore();
            return ResponseEntity.ok(new LoginResponse(id, username, role, score));

        } catch (LockedException ex) { // Catch block from Max
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "error", "ACCOUNT_BANNED",
                    "message", "Your account has been banned. Please contact an administrator."
            ));
        }
    }
}