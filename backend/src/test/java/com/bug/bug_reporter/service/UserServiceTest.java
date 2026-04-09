package com.bug.bug_reporter.service;

import com.bug.bug_reporter.dto.UserRegistrationDTO;
import com.bug.bug_reporter.dto.UserRequestDTO;
import com.bug.bug_reporter.dto.UserResponseDTO;
import com.bug.bug_reporter.model.User;
import com.bug.bug_reporter.model.UserRole;
import com.bug.bug_reporter.repository.UserRepository;
import com.bug.bug_reporter.utility.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("john_doe")
                .email("john@example.com")
                .phoneNumber("0712345678")
                .password("encodedPassword")
                .userRole(UserRole.USER)
                .build();
    }

    // ==================== saveUser ====================

    @Test
    void saveUser_shouldReturnUserResponseDTO() {
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO(
                "john_doe", "john@example.com", "password123", "0712345678"
        );

        doNothing().when(userValidator).validateRegistration(registrationDTO);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponseDTO result = userService.saveUser(registrationDTO);

        assertNotNull(result);
        assertEquals("john_doe", result.username());
        assertEquals("john@example.com", result.email());
        assertEquals(UserRole.USER, result.role());

        verify(userValidator).validateRegistration(registrationDTO);
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void saveUser_duplicateUsername_shouldThrowException() {
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO(
                "john_doe", "john@example.com", "password123", "0712345678"
        );

        doThrow(new UserAlreadyExistsException("Username is already taken"))
                .when(userValidator).validateRegistration(registrationDTO);

        assertThrows(UserAlreadyExistsException.class,
                () -> userService.saveUser(registrationDTO));

        verify(userRepository, never()).save(any(User.class));
    }

    // ==================== getUsers ====================

    @Test
    void getUsers_shouldReturnListOfUserResponseDTOs() {
        User secondUser = User.builder()
                .id(2L)
                .username("jane_doe")
                .email("jane@example.com")
                .password("encodedPassword2")
                .userRole(UserRole.MODERATOR)
                .build();

        when(userRepository.findAll()).thenReturn(List.of(testUser, secondUser));

        List<UserResponseDTO> result = userService.getUsers();

        assertEquals(2, result.size());
        assertEquals("john_doe", result.get(0).username());
        assertEquals("jane_doe", result.get(1).username());
    }

    @Test
    void getUsers_emptyList_shouldReturnEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserResponseDTO> result = userService.getUsers();

        assertTrue(result.isEmpty());
    }

    // ==================== getUserById ====================

    @Test
    void getUserById_existingId_shouldReturnUserResponseDTO() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserResponseDTO result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("john_doe", result.username());
    }

    @Test
    void getUserById_nonExistingId_shouldThrowUserNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(99L));
    }

    // ==================== deleteUser ====================

    @Test
    void deleteUser_existingId_shouldReturnDeletedUserDTO() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(testUser);

        UserResponseDTO result = userService.deleteUser(1L);

        assertNotNull(result);
        assertEquals("john_doe", result.username());
        verify(userRepository).delete(testUser);
    }

    @Test
    void deleteUser_nonExistingId_shouldThrowUserNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.deleteUser(99L));

        verify(userRepository, never()).delete(any(User.class));
    }
}
