package com.bug.bug_reporter.security;

import com.bug.bug_reporter.model.User;
import com.bug.bug_reporter.model.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import static org.assertj.core.api.Assertions.*;

class CustomUserDetailsTest {

    @Test
    @DisplayName("Should correctly construct CustomUserDetails from User entity")
    void constructor_MapsFieldsCorrectly() {
        // Arrange
        User user = User.builder()
                .id(42L)
                .username("johndoe")
                .email("john@example.com")
                .password("secureHash")
                .userRole(UserRole.USER)
                .build();

        // Act
        CustomUserDetails details = new CustomUserDetails(user);

        // Assert
        assertThat(details.getId()).isEqualTo(42L);
        assertThat(details.getUsername()).isEqualTo("johndoe");
        assertThat(details.getPassword()).isEqualTo("secureHash");
        assertThat(details.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_USER");
    }

    @Test
    @DisplayName("Should map MODERATOR role to ROLE_MODERATOR authority")
    void constructor_ModeratorRole() {
        // Arrange
        User moderator = User.builder()
                .id(1L)
                .username("admin")
                .password("hash")
                .userRole(UserRole.MODERATOR)
                .build();

        // Act
        CustomUserDetails details = new CustomUserDetails(moderator);

        // Assert
        assertThat(details.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_MODERATOR");
    }

    @Test
    @DisplayName("Should have exactly one authority")
    void constructor_SingleAuthority() {
        // Arrange
        User user = User.builder()
                .id(1L)
                .username("user1")
                .password("pass")
                .userRole(UserRole.USER)
                .build();

        // Act
        CustomUserDetails details = new CustomUserDetails(user);

        // Assert
        assertThat(details.getAuthorities()).hasSize(1);
    }
}
