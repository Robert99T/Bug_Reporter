package com.bug.bug_reporter.service;

import com.bug.bug_reporter.dto.BugResponse;
import com.bug.bug_reporter.dto.CommentResponse;
import com.bug.bug_reporter.dto.CreateBugRequest;
import com.bug.bug_reporter.dto.UpdateBugRequest;
import com.bug.bug_reporter.model.Bug;
import com.bug.bug_reporter.model.Comment;
import com.bug.bug_reporter.model.User;
import com.bug.bug_reporter.repository.BugRepository;
import com.bug.bug_reporter.repository.UserRepository;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;

@Service
public class BugService {

    private final BugRepository bugRepository;
    private final UserRepository userRepository;

    public BugService(BugRepository bugRepository, UserRepository userRepository) {
        this.bugRepository = bugRepository;
        this.userRepository = userRepository;
    }

    public BugResponse createBug(CreateBugRequest request) {
        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getAuthorId()));

        Bug bug = Bug.builder()
                .title(request.getTitle())
                .text(request.getText())
                .pictureUrl(request.getPictureUrl())
                .status(request.getStatus())
                .creationDate(LocalDateTime.now())
                .author(author)
                .build();

        Bug savedBug = bugRepository.save(bug);
        return mapToBugResponse(savedBug);
    }

    public List<BugResponse> getAllBugs() {
        return bugRepository.findAll()
                .stream()
                .map(this::mapToBugResponse)
                .toList();
    }

    public BugResponse getBugById(Long id) {
        Bug bug = bugRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bug not found with id: " + id));

        return mapToBugResponse(bug);
    }

    public BugResponse updateBug(Long id, UpdateBugRequest request) {
        Bug bug = bugRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bug not found with id: " + id));

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            bug.setTitle(request.getTitle());
        }

        if (request.getText() != null && !request.getText().isBlank()) {
            bug.setText(request.getText());
        }

        if (request.getPictureUrl() != null) {
            bug.setPictureUrl(request.getPictureUrl());
        }

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            bug.setStatus(request.getStatus());
        }

        Bug updatedBug = bugRepository.save(bug);
        return mapToBugResponse(updatedBug);
    }

    public void deleteBug(Long id) {
        Bug bug = bugRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bug not found with id: " + id));

        bugRepository.delete(bug);
    }

    private BugResponse mapToBugResponse(Bug bug) {
        BugResponse response = new BugResponse();
        response.setId(bug.getId());
        response.setTitle(bug.getTitle());
        response.setText(bug.getText());
        response.setCreationDate(bug.getCreationDate());
        response.setPictureUrl(bug.getPictureUrl());
        response.setStatus(bug.getStatus());

        if (bug.getAuthor() != null) {
            response.setAuthorId(bug.getAuthor().getId());
            response.setAuthorUsername(bug.getAuthor().getUsername());
        }

        if (bug.getComments() != null) {
            List<CommentResponse> comments = bug.getComments()
                    .stream()
                    .map(this::mapToCommentResponse)
                    .toList();
            response.setComments(comments);
        }

        return response;
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
