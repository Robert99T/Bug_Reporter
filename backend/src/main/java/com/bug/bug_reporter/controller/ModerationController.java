package com.bug.bug_reporter.controller;

import com.bug.bug_reporter.service.ModerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/moderation")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MODERATOR')")
public class ModerationController {

    private final ModerationService moderationService;

    @PostMapping("/users/{userId}/ban")
    public ResponseEntity<Map<String, String>> banUser(@PathVariable Long userId) {
        moderationService.banUser(userId);
        return ResponseEntity.ok(Map.of(
                "message", "User with id " + userId + " has been banned."
        ));
    }

    @PostMapping("/users/{userId}/unban")
    public ResponseEntity<Map<String, String>> unbanUser(@PathVariable Long userId) {
        moderationService.unbanUser(userId);
        return ResponseEntity.ok(Map.of(
                "message", "User with id " + userId + " has been unbanned."
        ));
    }
}
