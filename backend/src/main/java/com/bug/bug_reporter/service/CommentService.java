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
    private final com.bug.bug_reporter.repository.BugVoteRepository bugVoteRepository;
    private final com.bug.bug_reporter.repository.CommentVoteRepository commentVoteRepository;

    public CommentService(CommentRepository commentRepository,
                          BugRepository bugRepository,
                          UserRepository userRepository,
                          com.bug.bug_reporter.repository.BugVoteRepository bugVoteRepository,
                          com.bug.bug_reporter.repository.CommentVoteRepository commentVoteRepository) {
        this.commentRepository = commentRepository;
        this.bugRepository = bugRepository;
        this.userRepository = userRepository;
        this.bugVoteRepository = bugVoteRepository;
        this.commentVoteRepository = commentVoteRepository;
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
        return mapToCommentResponse(savedComment, null);
    }

    public List<CommentResponse> getCommentsByBugId(Long bugId, Long userId) {
        return commentRepository.findByBugId(bugId)
                .stream()
                .map(comment -> mapToCommentResponse(comment, userId))
                .toList();
    }

    public CommentResponse getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));

        return mapToCommentResponse(comment, null);
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
        return mapToCommentResponse(updatedComment, null);
    }

    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));

        commentRepository.delete(comment);
    }

    private CommentResponse mapToCommentResponse(Comment comment, Long userId) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setText(comment.getText());
        response.setPictureUrl(comment.getPictureUrl());
        response.setCreationDate(comment.getCreationDate());

        if (comment.getAuthor() != null) {
            response.setAuthorId(comment.getAuthor().getId());
            response.setAuthorUsername(comment.getAuthor().getUsername());
            Integer bugScore = bugVoteRepository.getAuthorVoteScore(comment.getAuthor().getId());
            Integer commentScore = commentVoteRepository.getAuthorVoteScore(comment.getAuthor().getId());
            double authorScore = (double) ((bugScore != null ? bugScore : 0) + (commentScore != null ? commentScore : 0));
            response.setAuthorScore(authorScore);
        }

        if (comment.getBug() != null) {
            response.setBugId(comment.getBug().getId());
        }

        Integer voteScore = commentVoteRepository.getVoteScoreByCommentId(comment.getId());
        response.setVoteScore(voteScore != null ? voteScore : 0);

        if (userId != null) {
            commentVoteRepository.findByUserIdAndCommentId(userId, comment.getId())
                    .ifPresent(vote -> response.setUserVote(vote.getVoteType() == 1 ? "UPVOTE" : "DOWNVOTE"));
        }

        return response;
    }
}
