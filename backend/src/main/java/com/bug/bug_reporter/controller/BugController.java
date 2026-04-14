package com.bug.bug_reporter.controller;

import com.bug.bug_reporter.dto.*;
import com.bug.bug_reporter.service.BugService;
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

    @GetMapping("/{id}")
    public ResponseEntity<BugResponse> getBugById(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId) {
        BugResponse bug = bugService.getBugById(id, userId);
        return ResponseEntity.ok(bug);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BugResponse> updateBug(@PathVariable Long id,
                                                 @RequestBody UpdateBugRequest request) {
        BugResponse updatedBug = bugService.updateBug(id, request);
        return ResponseEntity.ok(updatedBug);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBug(@PathVariable Long id) {
        bugService.deleteBug(id);
        return ResponseEntity.noContent().build();
    }


}
