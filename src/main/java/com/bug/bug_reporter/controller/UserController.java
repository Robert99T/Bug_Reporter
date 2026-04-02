package com.bug.bug_reporter.controller;

import com.bug.bug_reporter.dto.UserRegistrationDTO;
import com.bug.bug_reporter.dto.UserRequestDTO;
import com.bug.bug_reporter.dto.UserResponseDTO;
import com.bug.bug_reporter.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> saveUser(@Valid @RequestBody UserRegistrationDTO userRegistration) {
        UserResponseDTO response = userService.saveUser(userRegistration);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping()
    public ResponseEntity<List<UserResponseDTO>> getUsers() {
        List<UserResponseDTO> users = userService.getUsers();
        return ResponseEntity.status(HttpStatus.FOUND).body(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        UserResponseDTO user = userService.getUserById(id);
        return ResponseEntity.status(HttpStatus.FOUND).body(user);
    }

    @GetMapping("/remove")
    public ResponseEntity<UserResponseDTO> deleteUser(@RequestBody Integer userId) {
        UserResponseDTO deletedUser = userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body(deletedUser);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id,
            @Valid @RequestBody UserRequestDTO userRequestDTO) {

        UserResponseDTO updatedUser = userService.updateUser(id, userRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }

}
