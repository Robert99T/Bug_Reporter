package com.bug.bug_reporter.utility;

import com.bug.bug_reporter.dto.UserRegistrationDTO;
import com.bug.bug_reporter.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserValidator userValidator;

    @Test
    @DisplayName("Should pass validation when username and email are unique")
    void validateRegistration_Success() {
        // Arrange
        UserRegistrationDTO dto = new UserRegistrationDTO(
                "newuser", "new@example.com", "password123", "1234567890"
        );

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);

        // Act & Assert — should not throw
        assertThatCode(() -> userValidator.validateRegistration(dto))
                .doesNotThrowAnyException();

        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).existsByEmail("new@example.com");
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when username is taken")
    void validateRegistration_UsernameTaken() {
        // Arrange
        UserRegistrationDTO dto = new UserRegistrationDTO(
                "existinguser", "new@example.com", "password123", "1234567890"
        );

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userValidator.validateRegistration(dto))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("Username is already taken");

        // Email check should NOT be reached
        verify(userRepository, never()).existsByEmail(anyString());
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when email is already registered")
    void validateRegistration_EmailTaken() {
        // Arrange
        UserRegistrationDTO dto = new UserRegistrationDTO(
                "newuser", "existing@example.com", "password123", "1234567890"
        );

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userValidator.validateRegistration(dto))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("Email is already registered");
    }

    @Test
    @DisplayName("Should check username before email (order matters)")
    void validateRegistration_ChecksUsernameFirst() {
        // Arrange — both exist, but username error should fire first
        UserRegistrationDTO dto = new UserRegistrationDTO(
                "takenuser", "taken@example.com", "password123", "1234567890"
        );

        when(userRepository.existsByUsername("takenuser")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userValidator.validateRegistration(dto))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("Username is already taken");

        verify(userRepository, never()).existsByEmail(anyString());
    }
}
