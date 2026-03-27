package com.bug.bug_reporter.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "bugs")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Bug {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private String title; //

    @Setter
    @Column(nullable = false, columnDefinition = "TEXT")
    private String text; //

    @Setter
    @Column(nullable = false)
    private LocalDateTime creationDate; //

    @Setter
    private String pictureUrl; //

    @Setter
    @Column(nullable = false)
    private String status; // received, in progress, solved

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author; //

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "bug_tags",
            joinColumns = @JoinColumn(name = "bug_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private Set<Tag> tags = new HashSet<>(); //

    @OneToMany(mappedBy = "bug", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>(); //

    public void addTag(Tag tag) {
        tags.add(tag);
        tag.getBugs().add(this);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.getBugs().remove(this);
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setBug(this);
    }
}