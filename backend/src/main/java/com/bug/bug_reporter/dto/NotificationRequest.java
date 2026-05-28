package com.bug.bug_reporter.dto;

public record NotificationRequest (String email, String phoneNumber, String message) {
}
