package com.bug.bug_reporter.repository;

import com.bug.bug_reporter.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(@NotBlank String username);
    boolean existsByEmail(@Email @NotBlank String email);
}
