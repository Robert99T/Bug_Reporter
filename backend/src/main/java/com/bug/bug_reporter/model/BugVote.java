package com.bug.bug_reporter.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "bug_votes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "bug_id"}) // Ensures 1 vote per user per bug
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class BugVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bug_id", nullable = false)
    private Bug bug;

    @Setter
    @Column(nullable = false)
    private int voteType; // 1 for Upvote/Like, -1 for Downvote/Dislike
}