package com.bug.bug_reporter.dto;

import com.bug.bug_reporter.model.User;
import com.bug.bug_reporter.model.UserRole;

public record UserResponseDTO(
    Long id,
    String username,
    String email,
    String phoneNumber,
    double score,
    UserRole role
) {
    public static UserResponseDTO fromEntity(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getScore(),
                user.getUserRole()
        );
    }
}