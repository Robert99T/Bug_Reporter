package com.bug.bug_reporter.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponse {

    private Long id;
    private String text;
    private String pictureUrl;
    private LocalDateTime creationDate;
    private Long authorId;
    private String authorUsername;
    private Double authorScore;
    private Integer voteScore;
    private String userVote;
    private Long bugId;
}
