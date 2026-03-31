package com.bug.bug_reporter.controller;

import com.bug.bug_reporter.dto.BugResponse;
import com.bug.bug_reporter.dto.CreateBugRequest;
import com.bug.bug_reporter.dto.UserRegistrationDTO;
import com.bug.bug_reporter.dto.UserResponseDTO;
import com.bug.bug_reporter.service.BugService;
import com.bug.bug_reporter.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bugs")
@RequiredArgsConstructor

public class BugController {

    private final BugService bugService;

    @PostMapping
    public ResponseEntity<BugResponse> createBug(@Valid @RequestBody CreateBugRequest request) {
        BugResponse response = bugService.createBug(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<BugResponse>> getAllBugs() {
        List<BugResponse> response = bugService.getAllBugs();
        return ResponseEntity.ok(response);
    }


}
