package com.bug.bug_reporter.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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
}
