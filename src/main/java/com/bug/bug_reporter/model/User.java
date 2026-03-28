package com.bug.bug_reporter.model;

import com.bug.bug_reporter.model.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(unique = true, nullable = false)
    private String username;

    @Setter
    @Column(nullable = false)
    private String email;   // avem nevoie la bonus feature 2

    @Setter
    private String phoneNumber; // avem nevoie la bonus feature 2

    @JsonIgnore
    @Setter
    @Column(nullable = false)
    private String password; // Stored encrypted

    @Setter
    @Column(nullable = false)
    private Role role;

    @Setter
    @Builder.Default
    private double score = 0.0;

    @Setter
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