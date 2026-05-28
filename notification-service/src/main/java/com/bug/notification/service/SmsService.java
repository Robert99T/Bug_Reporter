package com.bug.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SmsService {

    private final RestTemplate restTemplate;

    @Value("${smspitt.url:http://localhost:2876}")
    private String smspittUrl;

    public void send(String phoneNumber, String message) {
        Map<String, String> body = Map.of(
                "to", phoneNumber,
                "from", "BugReporter",
                "message", message
        );
        restTemplate.postForEntity(smspittUrl + "/generic", body, String.class);
    }
}
