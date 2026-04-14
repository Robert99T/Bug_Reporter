package com.bug.bug_reporter.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VoteRequest {

    @NotNull
    private Long userId;

    @NotNull
    private String voteType; // "UPVOTE" or "DOWNVOTE"
}
