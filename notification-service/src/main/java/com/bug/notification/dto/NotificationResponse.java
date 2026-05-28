package com.bug.notification.dto;

import java.time.LocalDateTime;

public record NotificationResponse(
    Long id,
    String status,
    LocalDateTime sentAt
) {}