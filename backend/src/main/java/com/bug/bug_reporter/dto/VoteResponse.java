package com.bug.bug_reporter.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoteResponse {
    private Long id;
    private String targetType; // "BUG" or "COMMENT"
    private Long targetId;
    private Long userId;
    private String voteType; // "UPVOTE" or "DOWNVOTE"
}
