package com.bug.bug_reporter.service;

import com.bug.bug_reporter.dto.NotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class NotificationClient {

    private final RestTemplate restTemplate;

    @Value("${notification.service.url}")
    private String notificationServiceUrl;

    @Async
    public void sendBanNotification(String email, String phone, String message) {
        NotificationRequest request = new NotificationRequest(email, phone, message);
        try {
            restTemplate.exchange(
                    notificationServiceUrl + "/notifications",
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    String.class
            );
        } catch (RestClientException e) {
            throw new RuntimeException(e);
        }
    }
}