package com.bug.notification.service;

import com.bug.notification.dto.NotificationRequest;
import com.bug.notification.dto.NotificationResponse;
import com.bug.notification.model.Notification;
import com.bug.notification.model.NotificationStatus;
import com.bug.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final EmailService emailService;
    private final SmsService smsService;
    private final NotificationRepository notificationRepository;

    public NotificationResponse send(NotificationRequest request) {
        try {
            // 1. Send email
            emailService.send(request.email(), "Account Banned", request.message());

            // 2. Send SMS (mock)
            smsService.send(request.phoneNumber(), request.message());

            // 3. Persist success
            Notification notification = Notification.builder()
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .message(request.message())
                .status(NotificationStatus.SENT)
                .createdAt(LocalDateTime.now())
                .build();

            notificationRepository.save(notification);

            return new NotificationResponse(
                notification.getId(),
                notification.getStatus().name(),
                notification.getCreatedAt()
            );

        } catch (Exception e) {
            // 4. Persist failure
            Notification notification = Notification.builder()
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .message(request.message())
                .status(NotificationStatus.FAILED)
                .createdAt(LocalDateTime.now())
                .build();

            notificationRepository.save(notification);

            throw new RuntimeException("Notification failed", e);
        }
    }
}