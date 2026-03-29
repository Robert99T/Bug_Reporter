package com.bug.bug_reporter.utility;

import com.bug.bug_reporter.dto.UserRegistrationDTO;
import com.bug.bug_reporter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserRepository userRepository; // Injected by @RequiredArgsConstructor

    public void validateRegistration(UserRegistrationDTO dto) throws UserAlreadyExistsException {
        if (userRepository.existsByUsername(dto.username())) {
            throw new UserAlreadyExistsException("Username is already taken");
        }
        if (userRepository.existsByEmail(dto.email())) {
            throw new UserAlreadyExistsException("Email is already registered");
        }
    }
}