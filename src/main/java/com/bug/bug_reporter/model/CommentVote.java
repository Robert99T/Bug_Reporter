package com.bug.bug_reporter.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "comment_votes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "comment_id"}) // Ensures 1 vote per user per comment
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class CommentVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @Setter
    @Column(nullable = false)
    private int voteType; // 1 for Upvote, -1 for Downvote [cite: 25, 80]
}