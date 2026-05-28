package com.bug.bug_reporter.service;

import com.bug.bug_reporter.model.User;
import com.bug.bug_reporter.repository.UserRepository;
import com.bug.bug_reporter.utility.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ModerationService {

    private final UserRepository userRepository;
    private final NotificationClient notificationClient;

    @Transactional
    public void banUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found."));

        if (user.isBanned()) {
            throw new IllegalStateException("User is already banned.");
        }
        user.setBanned(true);

        notificationClient.sendBanNotification(
                user.getEmail(),
                user.getPhoneNumber() != null
                        ? user.getPhoneNumber()
                        : "no-phone",
                "Your account has been banned."
        );

    }

    @Transactional
    public void unbanUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found."));

        if (!user.isBanned()) {
            throw new IllegalStateException("User is not banned.");
        }
        user.setBanned(false);
    }
}
