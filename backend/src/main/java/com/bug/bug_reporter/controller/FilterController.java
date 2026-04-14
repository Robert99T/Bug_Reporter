package com.bug.bug_reporter.controller;

import com.bug.bug_reporter.dto.BugResponse;
import com.bug.bug_reporter.dto.UserResponseDTO;
import com.bug.bug_reporter.service.BugService;
import com.bug.bug_reporter.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FilterController {

    private final BugService bugService;
    private final UserService userService;

    @GetMapping("/tags")
    public ResponseEntity<List<String>> getAllTags() {
        return ResponseEntity.ok(bugService.getAllTags());
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
