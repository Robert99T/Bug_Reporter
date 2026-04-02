package com.bug.bug_reporter.dto;

import com.bug.bug_reporter.model.User;
import com.bug.bug_reporter.model.UserRole;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserResponseDTO(
    Long id,
    String username,
    String email,
    String phoneNumber,
    LocalDateTime registrationDate,
    double score,
    UserRole role
) {
    public static UserResponseDTO fromEntity(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRegistrationDate(),
                user.getScore(),
                user.getUserRole()
        );
    }
}