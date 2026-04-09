package com.bug.bug_reporter.dto;

import com.bug.bug_reporter.model.Tag;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class BugResponse {

    private Long id;
    private String title;
    private String text;
    private LocalDateTime creationDate;
    private String pictureUrl;
    private String status;
    private Long authorId;
    private String authorUsername;
    private List<CommentResponse> comments;
    private Set<String> tags;
}
