package com.bug.bug_reporter.config;

import com.bug.bug_reporter.model.User;
import com.bug.bug_reporter.model.UserRole;
import com.bug.bug_reporter.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "app.db.init-dev-user", havingValue = "true")
public class LocalUserSeeder {

    @Bean
    @Order(1)
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            if (userRepository.findByUsername("user").isEmpty()) {
                User user = User.builder()
                        .username("user")
                        .email("user@local.dev")
                        .password(passwordEncoder.encode("user"))
                        .userRole(UserRole.USER)
                        .isBanned(false)
                        .score(0.0)
                        .build();

                userRepository.save(user);
                log.info("User 'user' created successfully.");
            } else {
                log.debug("User 'user' already exists. Skipping.");
            }

            if (userRepository.findByUsername("user2").isEmpty()) {
                User admin = User.builder()
                        .username("user2")
                        .email("user2@local.dev")
                        .password(passwordEncoder.encode("user"))
                        .userRole(UserRole.USER)
                        .isBanned(false)
                        .score(100.0)
                        .build();

                userRepository.save(admin);
                log.info("User 'user2' created successfully.");
            } else {
                log.debug("User 'user2' already exists. Skipping.");
            }
        };
    }
}