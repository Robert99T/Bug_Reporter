package com.bug.bug_reporter.security;

import com.bug.bug_reporter.model.User;
import com.bug.bug_reporter.model.UserRole;
import com.bug.bug_reporter.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Should load user details successfully")
    void loadUserByUsername_Success() {
        // Arrange
        User user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .userRole(UserRole.USER)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act
        UserDetails result = customUserDetailsService.loadUserByUsername("testuser");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(CustomUserDetails.class);
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getPassword()).isEqualTo("encodedPassword");

        CustomUserDetails details = (CustomUserDetails) result;
        assertThat(details.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user not found")
    void loadUserByUsername_NotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("nonexistent"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("Should have correct authority for USER role")
    void loadUserByUsername_UserRole() {
        // Arrange
        User user = User.builder()
                .id(1L)
                .username("regularuser")
                .password("pass")
                .userRole(UserRole.USER)
                .build();

        when(userRepository.findByUsername("regularuser")).thenReturn(Optional.of(user));

        // Act
        UserDetails result = customUserDetailsService.loadUserByUsername("regularuser");

        // Assert
        assertThat(result.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_USER");
    }

    @Test
    @DisplayName("Should have correct authority for MODERATOR role")
    void loadUserByUsername_ModeratorRole() {
        // Arrange
        User moderator = User.builder()
                .id(2L)
                .username("moduser")
                .password("pass")
                .userRole(UserRole.MODERATOR)
                .build();

        when(userRepository.findByUsername("moduser")).thenReturn(Optional.of(moderator));

        // Act
        UserDetails result = customUserDetailsService.loadUserByUsername("moduser");

        // Assert
        assertThat(result.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_MODERATOR");
    }
}
