package com.bug.bug_reporter.service;

import com.bug.bug_reporter.dto.UserRegistrationDTO;
import com.bug.bug_reporter.dto.UserRequestDTO;
import com.bug.bug_reporter.dto.UserResponseDTO;
import com.bug.bug_reporter.model.User;
import com.bug.bug_reporter.model.UserRole;
import com.bug.bug_reporter.repository.UserRepository;
import com.bug.bug_reporter.security.CustomUserDetails;
import com.bug.bug_reporter.utility.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserValidator userValidator;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserRegistrationDTO registrationDTO;
    private UserRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .phoneNumber("1234567890")
                .password("encodedPassword")
                .userRole(UserRole.USER)
                .score(0.0)
                .build();

        registrationDTO = new UserRegistrationDTO(
                "testuser", "test@example.com", "password123", "1234567890"
        );

        requestDTO = new UserRequestDTO(
                "updateduser", "updated@example.com", "newpassword123", "0987654321"
        );
    }

    @Nested
    @DisplayName("saveUser Tests")
    class SaveUserTests {

        @Test
        @DisplayName("Should successfully register a new user")
        void saveUser_Success() {
            // Arrange
            doNothing().when(userValidator).validateRegistration(registrationDTO);
            when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // Act
            UserResponseDTO result = userService.saveUser(registrationDTO);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.username()).isEqualTo("testuser");
            assertThat(result.email()).isEqualTo("test@example.com");
            assertThat(result.role()).isEqualTo(UserRole.USER);

            verify(userValidator).validateRegistration(registrationDTO);
            verify(passwordEncoder).encode("password123");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when username already exists")
        void saveUser_UsernameAlreadyExists() {
            // Arrange
            doThrow(new UserAlreadyExistsException("Username is already taken"))
                    .when(userValidator).validateRegistration(registrationDTO);

            // Act & Assert
            assertThatThrownBy(() -> userService.saveUser(registrationDTO))
                    .isInstanceOf(UserAlreadyExistsException.class)
                    .hasMessage("Username is already taken");

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void saveUser_EmailAlreadyExists() {
            // Arrange
            doThrow(new UserAlreadyExistsException("Email is already registered"))
                    .when(userValidator).validateRegistration(registrationDTO);

            // Act & Assert
            assertThatThrownBy(() -> userService.saveUser(registrationDTO))
                    .isInstanceOf(UserAlreadyExistsException.class)
                    .hasMessage("Email is already registered");

            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("getUsers Tests")
    class GetUsersTests {

        @Test
        @DisplayName("Should return list of users")
        void getUsers_Success() {
            // Arrange
            User user2 = User.builder()
                    .id(2L)
                    .username("user2")
                    .email("user2@example.com")
                    .password("encoded")
                    .userRole(UserRole.MODERATOR)
                    .build();

            when(userRepository.findAll()).thenReturn(List.of(testUser, user2));

            // Act
            List<UserResponseDTO> result = userService.getUsers();

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result.get(0).username()).isEqualTo("testuser");
            assertThat(result.get(1).username()).isEqualTo("user2");
            assertThat(result.get(1).role()).isEqualTo(UserRole.MODERATOR);
        }

        @Test
        @DisplayName("Should return empty list when no users exist")
        void getUsers_EmptyList() {
            // Arrange
            when(userRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            List<UserResponseDTO> result = userService.getUsers();

            // Assert
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getUserById Tests")
    class GetUserByIdTests {

        @Test
        @DisplayName("Should return user when found")
        void getUserById_Success() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            // Act
            UserResponseDTO result = userService.getUserById(1L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.username()).isEqualTo("testuser");
            assertThat(result.email()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void getUserById_NotFound() {
            // Arrange
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> userService.getUserById(99L))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("not found");
        }
    }

    @Nested
    @DisplayName("deleteUser Tests")
    class DeleteUserTests {

        @Test
        @DisplayName("Should delete user and return deleted user data")
        void deleteUser_Success() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            doNothing().when(userRepository).delete(testUser);

            // Act
            UserResponseDTO result = userService.deleteUser(1L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.username()).isEqualTo("testuser");

            verify(userRepository).delete(testUser);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent user")
        void deleteUser_NotFound() {
            // Arrange
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> userService.deleteUser(99L))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("not found");

            verify(userRepository, never()).delete(any(User.class));
        }
    }

    @Nested
    @DisplayName("updateUser Tests")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user successfully when authorized")
        void updateUser_Success() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByUsername("updateduser")).thenReturn(false);
            when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
            when(passwordEncoder.matches("newpassword123", "encodedPassword")).thenReturn(false);
            when(passwordEncoder.encode("newpassword123")).thenReturn("newEncodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
                mockedSecurityUtils.when(() -> SecurityUtils.hasPermission(1L)).thenReturn(true);

                // Act
                UserResponseDTO result = userService.updateUser(1L, requestDTO);

                // Assert
                assertThat(result).isNotNull();
                verify(userRepository).save(any(User.class));
            }
        }

        @Test
        @DisplayName("Should throw AccessDeniedException when user lacks permission")
        void updateUser_PermissionDenied() {
            try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
                mockedSecurityUtils.when(() -> SecurityUtils.hasPermission(1L)).thenReturn(false);

                // Act & Assert
                assertThatThrownBy(() -> userService.updateUser(1L, requestDTO))
                        .isInstanceOf(AccessDeniedException.class)
                        .hasMessage("Permission denied!");

                verify(userRepository, never()).save(any(User.class));
            }
        }

        @Test
        @DisplayName("Should throw exception when updating to existing username")
        void updateUser_UsernameConflict() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByUsername("updateduser")).thenReturn(true);

            try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
                mockedSecurityUtils.when(() -> SecurityUtils.hasPermission(1L)).thenReturn(true);

                // Act & Assert
                assertThatThrownBy(() -> userService.updateUser(1L, requestDTO))
                        .isInstanceOf(UsernameAlreadyExistsException.class);

                verify(userRepository, never()).save(any(User.class));
            }
        }

        @Test
        @DisplayName("Should throw exception when updating to existing email")
        void updateUser_EmailConflict() {
            // Arrange — username is the same, so no username change
            UserRequestDTO sameUsernameDTO = new UserRequestDTO(
                    "testuser", "existing@example.com", "newpassword123", "0987654321"
            );

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

            try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
                mockedSecurityUtils.when(() -> SecurityUtils.hasPermission(1L)).thenReturn(true);

                // Act & Assert
                assertThatThrownBy(() -> userService.updateUser(1L, sameUsernameDTO))
                        .isInstanceOf(EmailAlreadyExistsException.class);

                verify(userRepository, never()).save(any(User.class));
            }
        }

        @Test
        @DisplayName("Should not re-encode password if it has not changed")
        void updateUser_PasswordUnchanged() {
            // Arrange — same username/email to skip those checks
            UserRequestDTO sameDataDTO = new UserRequestDTO(
                    "testuser", "test@example.com", "password123", "1234567890"
            );

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
                mockedSecurityUtils.when(() -> SecurityUtils.hasPermission(1L)).thenReturn(true);

                // Act
                userService.updateUser(1L, sameDataDTO);

                // Assert — encode should NOT be called since password matches
                verify(passwordEncoder, never()).encode(anyString());
            }
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user to update does not exist")
        void updateUser_UserNotFound() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
                mockedSecurityUtils.when(() -> SecurityUtils.hasPermission(99L)).thenReturn(true);

                assertThatThrownBy(() -> userService.updateUser(99L, requestDTO))
                        .isInstanceOf(UserNotFoundException.class);
            }
        }
    }
}
