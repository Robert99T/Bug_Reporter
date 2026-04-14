package com.bug.bug_reporter.repository;

import com.bug.bug_reporter.model.CommentVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentVoteRepository extends JpaRepository<CommentVote, Long> {
    Optional<CommentVote> findByUserIdAndCommentId(Long userId, Long commentId);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(v.voteType), 0) FROM CommentVote v WHERE v.comment.id = :commentId")
    Integer getVoteScoreByCommentId(@org.springframework.data.repository.query.Param("commentId") Long commentId);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(v.voteType), 0) FROM CommentVote v WHERE v.comment.author.id = :authorId")
    Integer getAuthorVoteScore(@org.springframework.data.repository.query.Param("authorId") Long authorId);
}
