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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@Transactional
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

        User voter = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        int newVoteTypeVal = "UPVOTE".equalsIgnoreCase(request.getVoteType()) ? 1 : -1;
        Optional<BugVote> existingOpt = bugVoteRepository.findByUserIdAndBugId(voter.getId(), bug.getId());

        // Clean optimization: Use the existing relation instead of a risky repository query
        User bugAuthor = bug.getAuthor();

        if (existingOpt.isPresent()) {
            BugVote existing = existingOpt.get();
            if (existing.getVoteType() == newVoteTypeVal) {
                // remove vote
                bugVoteRepository.delete(existing);
                if (newVoteTypeVal == 1) {
                    bugAuthor.decreaseScore(2.5);
                } else {
                    bugAuthor.increaseScore(1.5);
                }
                return null;
            } else {
                // switch vote
                existing.setVoteType(newVoteTypeVal);
                if (newVoteTypeVal == 1) {
                    bugAuthor.increaseScore(4.0);
                } else {
                    bugAuthor.decreaseScore(4.0);
                }
                return mapToDto(existing);
            }
        } else {
            BugVote newVote = BugVote.builder()
                    .user(voter)
                    .bug(bug)
                    .voteType(newVoteTypeVal)
                    .build();
            bugVoteRepository.save(newVote);

            if (newVoteTypeVal == 1) {
                bugAuthor.increaseScore(2.5);
            } else {
                bugAuthor.decreaseScore(1.5);
            }
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

        User voter = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        int newVoteTypeVal = "UPVOTE".equalsIgnoreCase(request.getVoteType()) ? 1 : -1;
        Optional<CommentVote> existingOpt = commentVoteRepository.findByUserIdAndCommentId(voter.getId(), comment.getId());

        // Clean optimization: Use the existing relation directly
        User commentAuthor = comment.getAuthor();

        if (existingOpt.isPresent()) {
            CommentVote existing = existingOpt.get();
            if (existing.getVoteType() == newVoteTypeVal) {
                // remove vote
                commentVoteRepository.delete(existing);
                if (newVoteTypeVal == 1) {
                    commentAuthor.decreaseScore(5.0);
                } else {
                    commentAuthor.increaseScore(2.5);
                    voter.increaseScore(1.5);
                }
                return null;
            } else {
                // switch vote
                existing.setVoteType(newVoteTypeVal);
                if (newVoteTypeVal == 1) {
                    commentAuthor.increaseScore(7.5);
                    voter.increaseScore(1.5);
                } else {
                    commentAuthor.decreaseScore(7.5);
                    voter.decreaseScore(1.5);
                }
                return mapToDto(existing);
            }
        } else {
            CommentVote newVote = CommentVote.builder()
                    .user(voter)
                    .comment(comment)
                    .voteType(newVoteTypeVal)
                    .build();
            commentVoteRepository.save(newVote);

            if (newVoteTypeVal == 1) {
                commentAuthor.increaseScore(5.0);
            } else {
                commentAuthor.decreaseScore(2.5);
                voter.decreaseScore(1.5);
            }
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

        User bugAuthor = vote.getBug().getAuthor();
        if (vote.getVoteType() == 1) {
            bugAuthor.decreaseScore(2.5);
        } else {
            bugAuthor.increaseScore(1.5);
        }
        bugVoteRepository.delete(vote);
    }

    public void removeCommentVote(Long commentId, Long voteId) {
        CommentVote vote = commentVoteRepository.findById(voteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vote not found"));

        if (!vote.getComment().getId().equals(commentId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vote does not belong to specified comment");
        }

        checkPermissions(vote.getUser().getId());

        User commentAuthor = vote.getComment().getAuthor();
        User voter = vote.getUser();
        if (vote.getVoteType() == 1) {
            commentAuthor.decreaseScore(5.0);
        } else {
            commentAuthor.increaseScore(2.5);
            voter.increaseScore(1.5);
        }
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
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to perform this action");
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