package com.bug.notification.dto;

import jakarta.validation.constraints.NotBlank;

public record NotificationRequest(
    @NotBlank String email,
    @NotBlank String phoneNumber,
    @NotBlank String message
) {}