package com.bug.bug_reporter.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;

@Data
public class CreateBugRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String text;

    private String pictureUrl;

    @NotBlank
    private String status;

    private Long authorId;

    private Set<String> tags;


}
