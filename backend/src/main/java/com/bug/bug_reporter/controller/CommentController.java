package com.bug.bug_reporter.controller;

import com.bug.bug_reporter.dto.CommentResponse;
import com.bug.bug_reporter.dto.CreateCommentRequest;
import com.bug.bug_reporter.dto.UpdateCommentRequest;
import com.bug.bug_reporter.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/bugs/{bugId}/comments")
    public ResponseEntity<CommentResponse> createComment(@PathVariable Long bugId,
                                                         @Valid @RequestBody CreateCommentRequest request) {
        CommentResponse response = commentService.createComment(bugId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/bugs/{bugId}/comments")
    public ResponseEntity<List<CommentResponse>> getCommentsByBugId(
            @PathVariable Long bugId,
            @RequestParam(required = false) Long userId) {
        List<CommentResponse> comments = commentService.getCommentsByBugId(bugId, userId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/comments/{id}")
    public ResponseEntity<CommentResponse> getCommentById(@PathVariable Long id) {
        CommentResponse comment = commentService.getCommentById(id);
        return ResponseEntity.ok(comment);
    }

    @PutMapping("/comments/{id}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long id,
                                                         @RequestBody UpdateCommentRequest request) {
        CommentResponse updatedComment = commentService.updateComment(id, request);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
