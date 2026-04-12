package com.bug.bug_reporter.repository;

import com.bug.bug_reporter.model.CommentVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentVoteRepository extends JpaRepository<CommentVote, Long> {
    Optional<CommentVote> findByUserIdAndCommentId(Long userId, Long commentId);
}
