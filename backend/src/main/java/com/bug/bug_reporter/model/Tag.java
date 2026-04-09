package com.bug.bug_reporter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tags")
@Data
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(unique = true, nullable = false)
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "tags")
    @Builder.Default
    private Set<Bug> bugs = new HashSet<>();


    public Tag(String name) {
        this.name = name;
    }
}
