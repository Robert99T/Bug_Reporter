package com.bug.bug_reporter.service;

import com.bug.bug_reporter.dto.UserRegistrationDTO;
import com.bug.bug_reporter.dto.UserResponseDTO;
import com.bug.bug_reporter.model.User;
import com.bug.bug_reporter.model.UserRole;
import com.bug.bug_reporter.repository.UserRepository;
import com.bug.bug_reporter.utility.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDTO saveUser(@RequestBody UserRegistrationDTO userRegistration) {
        userValidator.validateRegistration(userRegistration);

        String encodedPassword = passwordEncoder.encode(userRegistration.password());

        User user = User.builder()
                .username(userRegistration.username())
                .email(userRegistration.email())
                .phoneNumber(userRegistration.phoneNumber())
                .password(encodedPassword)
                .userRole(UserRole.USER)
                .build();

        User savedUser = userRepository.save(user);

        return UserResponseDTO.fromEntity(savedUser);
    }

    public List<UserResponseDTO> getUsers() {
        return userRepository.findAll().stream()
                .map(UserResponseDTO::fromEntity)
                .toList();
    }
}
