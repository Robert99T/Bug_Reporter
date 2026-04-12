package com.bug.bug_reporter.repository;

import com.bug.bug_reporter.model.BugVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BugVoteRepository extends JpaRepository<BugVote, Long> {
    Optional<BugVote> findByUserIdAndBugId(Long userId, Long bugId);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(v.voteType), 0) FROM BugVote v WHERE v.bug.id = :bugId")
    Integer getVoteScoreByBugId(@org.springframework.data.repository.query.Param("bugId") Long bugId);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(v.voteType), 0) FROM BugVote v WHERE v.bug.author.id = :authorId")
    Integer getAuthorVoteScore(@org.springframework.data.repository.query.Param("authorId") Long authorId);
}
