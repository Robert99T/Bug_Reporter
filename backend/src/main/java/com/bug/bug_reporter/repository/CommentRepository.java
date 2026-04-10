package com.bug.bug_reporter.repository;

import com.bug.bug_reporter.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBugId(Long bugId);
}
