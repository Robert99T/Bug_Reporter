package com.bug.bug_reporter.dto;

import com.bug.bug_reporter.model.User;
import com.bug.bug_reporter.model.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class UserResponseDTOTest {

    @Test
    @DisplayName("Should correctly map User entity to UserResponseDTO")
    void fromEntity_MapsAllFields() {
        // Arrange
        User user = User.builder()
                .id(1L)
                .username("johndoe")
                .email("john@example.com")
                .phoneNumber("555-1234")
                .password("encodedPassword")
                .userRole(UserRole.USER)
                .score(4.5)
                .build();

        // Act
        UserResponseDTO dto = UserResponseDTO.fromEntity(user);

        // Assert
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.username()).isEqualTo("johndoe");
        assertThat(dto.email()).isEqualTo("john@example.com");
        assertThat(dto.phoneNumber()).isEqualTo("555-1234");
        assertThat(dto.score()).isEqualTo(4.5);
        assertThat(dto.role()).isEqualTo(UserRole.USER);
    }

    @Test
    @DisplayName("Should map user with MODERATOR role correctly")
    void fromEntity_ModeratorRole() {
        // Arrange
        User moderator = User.builder()
                .id(2L)
                .username("moduser")
                .email("mod@example.com")
                .password("encoded")
                .userRole(UserRole.MODERATOR)
                .score(10.0)
                .build();

        // Act
        UserResponseDTO dto = UserResponseDTO.fromEntity(moderator);

        // Assert
        assertThat(dto.role()).isEqualTo(UserRole.MODERATOR);
        assertThat(dto.score()).isEqualTo(10.0);
    }

    @Test
    @DisplayName("Should handle null phoneNumber")
    void fromEntity_NullPhoneNumber() {
        // Arrange
        User user = User.builder()
                .id(3L)
                .username("noPhone")
                .email("nophone@example.com")
                .password("encoded")
                .userRole(UserRole.USER)
                .build();

        // Act
        UserResponseDTO dto = UserResponseDTO.fromEntity(user);

        // Assert
        assertThat(dto.phoneNumber()).isNull();
    }

    @Test
    @DisplayName("Should have default score of 0.0")
    void fromEntity_DefaultScore() {
        // Arrange
        User user = User.builder()
                .id(4L)
                .username("newuser")
                .email("new@example.com")
                .password("encoded")
                .userRole(UserRole.USER)
                .build();

        // Act
        UserResponseDTO dto = UserResponseDTO.fromEntity(user);

        // Assert
        assertThat(dto.score()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should not expose password in DTO")
    void fromEntity_NoPasswordField() {
        // The UserResponseDTO record does not have a password field.
        // This test validates the design: password is excluded from the response.
        User user = User.builder()
                .id(1L)
                .username("user")
                .email("user@example.com")
                .password("superSecret")
                .userRole(UserRole.USER)
                .build();

        UserResponseDTO dto = UserResponseDTO.fromEntity(user);

        // Assert — the DTO toString should NOT contain the password
        assertThat(dto.toString()).doesNotContain("superSecret");
    }
}
