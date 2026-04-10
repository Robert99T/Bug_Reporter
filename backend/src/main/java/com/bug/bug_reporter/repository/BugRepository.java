package com.bug.bug_reporter.repository;

import com.bug.bug_reporter.model.Bug;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BugRepository extends JpaRepository<Bug, Long>{
    boolean existsByTitle(String title);
}
