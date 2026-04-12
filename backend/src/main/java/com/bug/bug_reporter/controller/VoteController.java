package com.bug.bug_reporter.controller;

import com.bug.bug_reporter.dto.VoteRequest;
import com.bug.bug_reporter.dto.VoteResponse;
import com.bug.bug_reporter.service.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping("/bugs/{bugId}/votes")
    public ResponseEntity<VoteResponse> voteOnBug(@PathVariable Long bugId, @Valid @RequestBody VoteRequest request) {
        VoteResponse response = voteService.voteOnBug(bugId, request);
        if (response == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/comments/{commentId}/votes")
    public ResponseEntity<VoteResponse> voteOnComment(@PathVariable Long commentId, @Valid @RequestBody VoteRequest request) {
        VoteResponse response = voteService.voteOnComment(commentId, request);
        if (response == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/bugs/{bugId}/votes/{voteId}")
    public ResponseEntity<Void> removeBugVote(@PathVariable Long bugId, @PathVariable Long voteId) {
        voteService.removeBugVote(bugId, voteId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/comments/{commentId}/votes/{voteId}")
    public ResponseEntity<Void> removeCommentVote(@PathVariable Long commentId, @PathVariable Long voteId) {
        voteService.removeCommentVote(commentId, voteId);
        return ResponseEntity.noContent().build();
    }
}
