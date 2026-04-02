package com.bug.bug_reporter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;   // avem nevoie la bonus feature 2

    private String phoneNumber; // avem nevoie la bonus feature 2

    @JsonIgnore
    @Column(nullable = false)
    private String password; // Stored encrypted

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole userRole;

    @Builder.Default
    private double score = 0.0;

    @Builder.Default
    private boolean isBanned = false;

    @JsonIgnore
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Bug> bugs = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    public void addBug(Bug bug) {
        bugs.add(bug);
        bug.setAuthor(this);
    }

    public void removeBug(Bug bug) {
        bugs.remove(bug);
        bug.setAuthor(null);
    }
}