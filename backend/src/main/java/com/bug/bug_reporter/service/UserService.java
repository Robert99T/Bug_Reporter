package com.bug.bug_reporter.service;

import com.bug.bug_reporter.dto.UserRegistrationDTO;
import com.bug.bug_reporter.dto.UserRequestDTO;
import com.bug.bug_reporter.dto.UserResponseDTO;
import com.bug.bug_reporter.dto.UserScoreResponse;
import com.bug.bug_reporter.model.User;
import com.bug.bug_reporter.model.UserRole;
import com.bug.bug_reporter.repository.UserRepository;
import com.bug.bug_reporter.utility.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
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

    public List<UserResponseDTO> getAllUsers() {
        return getUsers();
    }

    public UserResponseDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found!"));
        return UserResponseDTO.fromEntity(user);
    }

    public UserScoreResponse getUserScore(Long userId) {
        // TODO: Implement logic to calculate and return user's score
        // 1. Check if user exists. If not, throw UserNotFoundException (handled as 404 Not Found)
        // 2. Calculate the score
        // 3. Construct and return UserScoreResponse
        return new UserScoreResponse(userId, 0.0);
    }

    public UserResponseDTO deleteUser(Long userId) {
        User userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));
        userRepository.delete(userToDelete);
        return UserResponseDTO.fromEntity(userToDelete);
    }

    @Transactional
    public UserResponseDTO updateUser(Long id, UserRequestDTO userRequestDTO) {

        if(!SecurityUtils.hasPermission(id)) {
            throw new AccessDeniedException("Permission denied!");
        }

        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));

        updateUsernameIfChanged(userToUpdate, userRequestDTO.username());
        updateEmailIfChanged(userToUpdate, userRequestDTO.email());
        updatePasswordIfChanged(userToUpdate, userRequestDTO.password());
        userToUpdate.setPhoneNumber(userRequestDTO.phoneNumber());

        User updatedUser = userRepository.save(userToUpdate);

        return UserResponseDTO.fromEntity(updatedUser);
    }


    private void updateUsernameIfChanged(User user, String newUsername) {
        if (user.getUsername().equals(newUsername)) return;

        if (userRepository.existsByUsername(newUsername)) {
            throw new UsernameAlreadyExistsException("Username " + newUsername + " already exists!");
        }
        user.setUsername(newUsername);
    }

    private void updateEmailIfChanged(User user, String newEmail) {
        if (user.getEmail().equals(newEmail)) return;

        if (userRepository.existsByEmail(newEmail)) {
            throw new EmailAlreadyExistsException("Email " + newEmail + " already exists!");
        }
        user.setEmail(newEmail);
    }

    private void updatePasswordIfChanged(User user, String newRawPassword) {
        if (newRawPassword != null && !passwordEncoder.matches(newRawPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newRawPassword));
        }
    }

    /**
     * TODO: Implement the specific author score calculation logic here. (Bonus feature 1)
     * * @param authorId The ID of the author to calculate the score for.
     * @return The calculated author score.
     */
    public double calculateUserScore(Long authorId) {
        // Placeholder value until the specific calculation algorithm is implemented
        return 0.0;
    }

}
