package com.bug.bug_reporter.service;

import com.bug.bug_reporter.dto.CommentResponse;
import com.bug.bug_reporter.dto.CreateCommentRequest;
import com.bug.bug_reporter.dto.UpdateCommentRequest;
import com.bug.bug_reporter.model.Bug;
import com.bug.bug_reporter.model.Comment;
import com.bug.bug_reporter.model.User;
import com.bug.bug_reporter.repository.BugRepository;
import com.bug.bug_reporter.repository.CommentRepository;
import com.bug.bug_reporter.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final BugRepository bugRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository,
                          BugRepository bugRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.bugRepository = bugRepository;
        this.userRepository = userRepository;
    }

    public CommentResponse createComment(Long bugId, CreateCommentRequest request) {
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new RuntimeException("Bug not found with id: " + bugId));

        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getAuthorId()));

        Comment comment = Comment.builder()
                .text(request.getText())
                .pictureUrl(request.getPictureUrl())
                .creationDate(LocalDateTime.now())
                .author(author)
                .bug(bug)
                .build();

        Comment savedComment = commentRepository.save(comment);
        return mapToCommentResponse(savedComment);
    }

    public List<CommentResponse> getCommentsByBugId(Long bugId) {
        return commentRepository.findByBugId(bugId)
                .stream()
                .map(this::mapToCommentResponse)
                .toList();
    }

    public CommentResponse getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));

        return mapToCommentResponse(comment);
    }

    public CommentResponse updateComment(Long id, UpdateCommentRequest request) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));

        if (request.getText() != null && !request.getText().isBlank()) {
            comment.setText(request.getText());
        }

        if (request.getPictureUrl() != null) {
            comment.setPictureUrl(request.getPictureUrl());
        }

        Comment updatedComment = commentRepository.save(comment);
        return mapToCommentResponse(updatedComment);
    }

    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));

        commentRepository.delete(comment);
    }

    private CommentResponse mapToCommentResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setText(comment.getText());
        response.setPictureUrl(comment.getPictureUrl());
        response.setCreationDate(comment.getCreationDate());

        if (comment.getAuthor() != null) {
            response.setAuthorId(comment.getAuthor().getId());
            response.setAuthorUsername(comment.getAuthor().getUsername());
        }

        if (comment.getBug() != null) {
            response.setBugId(comment.getBug().getId());
        }

        return response;
    }
}
