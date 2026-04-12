package com.bug.bug_reporter.service;

import com.bug.bug_reporter.dto.VoteRequest;
import com.bug.bug_reporter.dto.VoteResponse;
import com.bug.bug_reporter.model.*;
import com.bug.bug_reporter.repository.*;
import com.bug.bug_reporter.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final BugVoteRepository bugVoteRepository;
    private final CommentVoteRepository commentVoteRepository;
    private final BugRepository bugRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public VoteResponse voteOnBug(Long bugId, VoteRequest request) {
        checkPermissions(request.getUserId());

        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bug not found"));

        if (bug.getAuthor().getId().equals(request.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot vote on your own bug");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        int voteTypeVal = "UPVOTE".equalsIgnoreCase(request.getVoteType()) ? 1 : -1;

        Optional<BugVote> existingOpt = bugVoteRepository.findByUserIdAndBugId(user.getId(), bug.getId());

        if (existingOpt.isPresent()) {
            BugVote existing = existingOpt.get();
            if (existing.getVoteType() == voteTypeVal) {
                // remove vote
                bugVoteRepository.delete(existing);
                return null;
            } else {
                // switch vote
                existing.setVoteType(voteTypeVal);
                bugVoteRepository.save(existing);
                return mapToDto(existing);
            }
        } else {
            BugVote newVote = BugVote.builder()
                    .user(user)
                    .bug(bug)
                    .voteType(voteTypeVal)
                    .build();
            bugVoteRepository.save(newVote);
            return mapToDto(newVote);
        }
    }

    public VoteResponse voteOnComment(Long commentId, VoteRequest request) {
        checkPermissions(request.getUserId());

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        if (comment.getAuthor().getId().equals(request.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot vote on your own comment");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        int voteTypeVal = "UPVOTE".equalsIgnoreCase(request.getVoteType()) ? 1 : -1;

        Optional<CommentVote> existingOpt = commentVoteRepository.findByUserIdAndCommentId(user.getId(), comment.getId());

        if (existingOpt.isPresent()) {
            CommentVote existing = existingOpt.get();
            if (existing.getVoteType() == voteTypeVal) {
                // remove vote
                commentVoteRepository.delete(existing);
                return null;
            } else {
                // switch vote
                existing.setVoteType(voteTypeVal);
                commentVoteRepository.save(existing);
                return mapToDto(existing);
            }
        } else {
            CommentVote newVote = CommentVote.builder()
                    .user(user)
                    .comment(comment)
                    .voteType(voteTypeVal)
                    .build();
            commentVoteRepository.save(newVote);
            return mapToDto(newVote);
        }
    }

    public void removeBugVote(Long bugId, Long voteId) {
        BugVote vote = bugVoteRepository.findById(voteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vote not found"));
        
        if (!vote.getBug().getId().equals(bugId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vote does not belong to specified bug");
        }

        checkPermissions(vote.getUser().getId());
        bugVoteRepository.delete(vote);
    }

    public void removeCommentVote(Long commentId, Long voteId) {
        CommentVote vote = commentVoteRepository.findById(voteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vote not found"));
        
        if (!vote.getComment().getId().equals(commentId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vote does not belong to specified comment");
        }

        checkPermissions(vote.getUser().getId());
        commentVoteRepository.delete(vote);
    }

    private void checkPermissions(Long voteOwnerId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        
        Object principal = auth.getPrincipal();
        if (!(principal instanceof CustomUserDetails)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid principal");
        }
        
        CustomUserDetails userDetails = (CustomUserDetails) principal;
        boolean isOwner = voteOwnerId.equals(userDetails.getId());
        boolean isModerator = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MODERATOR"));

        if (!isOwner && !isModerator) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to perform this action for this user (must be logged in or admin)");
        }
    }

    private VoteResponse mapToDto(BugVote vote) {
        return VoteResponse.builder()
                .id(vote.getId())
                .targetType("BUG")
                .targetId(vote.getBug().getId())
                .userId(vote.getUser().getId())
                .voteType(vote.getVoteType() == 1 ? "UPVOTE" : "DOWNVOTE")
                .build();
    }

    private VoteResponse mapToDto(CommentVote vote) {
        return VoteResponse.builder()
                .id(vote.getId())
                .targetType("COMMENT")
                .targetId(vote.getComment().getId())
                .userId(vote.getUser().getId())
                .voteType(vote.getVoteType() == 1 ? "UPVOTE" : "DOWNVOTE")
                .build();
    }
}
