package com.bug.bug_reporter.repository;

import com.bug.bug_reporter.model.BugVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BugVoteRepository extends JpaRepository<BugVote, Long> {
    Optional<BugVote> findByUserIdAndBugId(Long userId, Long bugId);
}
