package com.bug.bug_reporter.config;

import com.bug.bug_reporter.model.User;
import com.bug.bug_reporter.model.UserRole;
import com.bug.bug_reporter.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "app.db.init-dev-user", havingValue = "true")
public class LocalUserSeeder {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("user").isEmpty()) {
                User devUser = User.builder()
                        .username("user")
                        .email("user@local.dev")
                        .password(passwordEncoder.encode("user"))
                        .userRole(UserRole.USER)
                        .isBanned(false)
                        .score(0.0)
                        .build();

                userRepository.save(devUser);
                log.info("Local development user 'dev_tester' created successfully.");
            } else {
                log.debug("Dev user already exists. Skipping initialization.");
            }
        };
    }
}