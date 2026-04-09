package com.bug.bug_reporter.config;

import com.bug.bug_reporter.model.Bug;
import com.bug.bug_reporter.model.Comment;
import com.bug.bug_reporter.model.Tag;
import com.bug.bug_reporter.model.User;
import com.bug.bug_reporter.repository.BugRepository;
import com.bug.bug_reporter.repository.CommentRepository;
import com.bug.bug_reporter.repository.TagRepository;
import com.bug.bug_reporter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;
import java.time.LocalDateTime;
import java.util.Set;

import static com.bug.bug_reporter.utility.Utility.getOrCreateTag;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "app.db.init-dev-bugs", havingValue = "true")
@RequiredArgsConstructor
public class LocalBugSeeder {

    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;

    @Bean
    @Order(2)
    CommandLineRunner initBugs(BugRepository bugRepository, UserRepository userRepository) {
        return args -> {
            User devUser = userRepository.findByUsername("user")
                    .orElseThrow(() -> new RuntimeException("Dev user not found!"));

            User devUser2 = userRepository.findByUsername("user2")
                    .orElseThrow(() -> new RuntimeException("Dev user2 not found!"));

            Bug bug1 = Bug.builder()
                    .title("Login page error")
                    .text("Login fails when using special characters in the password")
                    .creationDate(LocalDateTime.now())
                    .status("OPEN")
                    .author(devUser)
                    .tags(Set.of(
                            getOrCreateTag("backend", tagRepository),
                            getOrCreateTag("auth", tagRepository),
                            getOrCreateTag("critical", tagRepository)
                    ))
                    .build();

            Bug bug2 = Bug.builder()
                    .title("UI glitch on dashboard")
                    .text("Dashboard widgets overlap on small screens")
                    .creationDate(LocalDateTime.now())
                    .status("OPEN")
                    .author(devUser2)
                    .tags(Set.of(
                            getOrCreateTag("frontend", tagRepository),
                            getOrCreateTag("ui", tagRepository),
                            getOrCreateTag("responsive", tagRepository)
                    ))
                    .build();

            Bug bug3 = Bug.builder()
                    .title("Registration email not sent")
                    .text("Users do not receive confirmation emails after signing up")
                    .creationDate(LocalDateTime.now())
                    .status("OPEN")
                    .author(devUser)
                    .tags(Set.of(
                            getOrCreateTag("backend", tagRepository),
                            getOrCreateTag("email", tagRepository),
                            getOrCreateTag("bug", tagRepository)
                    ))
                    .build();

            Bug bug4 = Bug.builder()
                    .title("Profile picture upload fails")
                    .text("Uploading profile pictures results in a 500 error")
                    .creationDate(LocalDateTime.now())
                    .status("OPEN")
                    .author(devUser)
                    .tags(Set.of(
                            getOrCreateTag("backend", tagRepository),
                            getOrCreateTag("file-upload", tagRepository),
                            getOrCreateTag("error", tagRepository)
                    ))
                    .build();

            Bug bug5 = Bug.builder()
                    .title("Search function slow")
                    .text("Search takes too long when querying large datasets")
                    .creationDate(LocalDateTime.now())
                    .status("OPEN")
                    .author(devUser2)
                    .tags(Set.of(
                            getOrCreateTag("performance", tagRepository),
                            getOrCreateTag("backend", tagRepository),
                            getOrCreateTag("optimization", tagRepository)
                    ))
                    .build();

            Bug bug6 = Bug.builder()
                    .title("Password reset broken")
                    .text("Reset link expires immediately after generation")
                    .creationDate(LocalDateTime.now())
                    .status("OPEN")
                    .author(devUser)
                    .tags(Set.of(
                            getOrCreateTag("auth", tagRepository),
                            getOrCreateTag("security", tagRepository),
                            getOrCreateTag("bug", tagRepository)
                    ))
                    .build();

            Bug bug7 = Bug.builder()
                    .title("Notifications not updating")
                    .text("Users do not see new notifications unless page is refreshed")
                    .creationDate(LocalDateTime.now())
                    .status("OPEN")
                    .author(devUser2)
                    .tags(Set.of(
                            getOrCreateTag("frontend", tagRepository),
                            getOrCreateTag("realtime", tagRepository),
                            getOrCreateTag("ui", tagRepository)
                    ))
                    .build();

            List<Bug> bugs = List.of(bug1, bug2, bug3, bug4, bug5, bug6, bug7);
            if (bugRepository.count() == 0) {
                bugRepository.saveAll(List.of(
                        bug1, bug2, bug3, bug4, bug5, bug6, bug7
                ));


                Bug bug1Saved = bugs.getFirst();

                Comment c1 = new Comment();
                c1.setText("First comment");
                c1.setCreationDate(LocalDateTime.now());
                c1.setAuthor(devUser);
                c1.setBug(bug1Saved);

                commentRepository.save(c1);
            }




            log.info("Sample bugs added successfully.");
        };
    }



}