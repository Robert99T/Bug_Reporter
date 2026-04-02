package com.bug.bug_reporter.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class ModelTests {

    @Nested
    @DisplayName("User Model Tests")
    class UserModelTests {

        private User user;

        @BeforeEach
        void setUp() {
            user = User.builder()
                    .id(1L)
                    .username("testuser")
                    .email("test@example.com")
                    .password("encoded")
                    .userRole(UserRole.USER)
                    .build();
        }

        @Test
        @DisplayName("Should have default score of 0.0")
        void defaultScore() {
            assertThat(user.getScore()).isEqualTo(0.0);
        }

        @Test
        @DisplayName("Should have default isBanned as false")
        void defaultIsBanned() {
            assertThat(user.isBanned()).isFalse();
        }

        @Test
        @DisplayName("Should have empty bug list by default")
        void defaultBugList() {
            assertThat(user.getBugs()).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("Should have empty comment list by default")
        void defaultCommentList() {
            assertThat(user.getComments()).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("addBug should add bug and set author")
        void addBug_SetsAuthor() {
            Bug bug = Bug.builder()
                    .id(10L)
                    .title("A Bug")
                    .text("Description")
                    .status("OPEN")
                    .creationDate(LocalDateTime.now())
                    .build();

            user.addBug(bug);

            assertThat(user.getBugs()).hasSize(1);
            assertThat(user.getBugs().get(0)).isEqualTo(bug);
            assertThat(bug.getAuthor()).isEqualTo(user);
        }

        @Test
        @DisplayName("removeBug should remove bug and unset author")
        void removeBug_UnsetsAuthor() {
            Bug bug = Bug.builder()
                    .id(10L)
                    .title("A Bug")
                    .text("Description")
                    .status("OPEN")
                    .creationDate(LocalDateTime.now())
                    .build();

            user.addBug(bug);
            user.removeBug(bug);

            assertThat(user.getBugs()).isEmpty();
            assertThat(bug.getAuthor()).isNull();
        }

        @Test
        @DisplayName("Equality should be based on id only")
        void equalityById() {
            User user2 = User.builder()
                    .id(1L)
                    .username("different")
                    .email("different@example.com")
                    .password("other")
                    .userRole(UserRole.MODERATOR)
                    .build();

            assertThat(user).isEqualTo(user2);
            assertThat(user.hashCode()).isEqualTo(user2.hashCode());
        }

        @Test
        @DisplayName("Users with different ids should not be equal")
        void inequalityByDifferentId() {
            User user2 = User.builder()
                    .id(2L)
                    .username("testuser")
                    .email("test@example.com")
                    .password("encoded")
                    .userRole(UserRole.USER)
                    .build();

            assertThat(user).isNotEqualTo(user2);
        }
    }

    @Nested
    @DisplayName("Bug Model Tests")
    class BugModelTests {

        private Bug bug;
        private User author;

        @BeforeEach
        void setUp() {
            author = User.builder()
                    .id(1L)
                    .username("author")
                    .email("author@example.com")
                    .password("enc")
                    .userRole(UserRole.USER)
                    .build();

            bug = Bug.builder()
                    .id(1L)
                    .title("Test Bug")
                    .text("Bug description")
                    .status("OPEN")
                    .creationDate(LocalDateTime.of(2026, 4, 1, 10, 0))
                    .author(author)
                    .build();
        }

        @Test
        @DisplayName("Should have empty tags set by default")
        void defaultTags() {
            assertThat(bug.getTags()).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("Should have empty comments list by default")
        void defaultComments() {
            assertThat(bug.getComments()).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("addTag should add tag and link bug to tag")
        void addTag_LinksBugToTag() {
            Tag tag = Tag.builder()
                    .id(1L)
                    .name("UI")
                    .build();

            bug.addTag(tag);

            assertThat(bug.getTags()).contains(tag);
            assertThat(tag.getBugs()).contains(bug);
        }

        @Test
        @DisplayName("removeTag should remove tag and unlink bug from tag")
        void removeTag_UnlinksBugFromTag() {
            Tag tag = Tag.builder()
                    .id(1L)
                    .name("UI")
                    .build();

            bug.addTag(tag);
            bug.removeTag(tag);

            assertThat(bug.getTags()).doesNotContain(tag);
            assertThat(tag.getBugs()).doesNotContain(bug);
        }

        @Test
        @DisplayName("addComment should add comment and set bug reference")
        void addComment_SetsBugRef() {
            Comment comment = Comment.builder()
                    .id(1L)
                    .text("A comment")
                    .creationDate(LocalDateTime.now())
                    .author(author)
                    .build();

            bug.addComment(comment);

            assertThat(bug.getComments()).hasSize(1);
            assertThat(comment.getBug()).isEqualTo(bug);
        }

        @Test
        @DisplayName("Equality should be based on id only")
        void equalityById() {
            Bug bug2 = Bug.builder()
                    .id(1L)
                    .title("Different Title")
                    .text("Different Text")
                    .status("CLOSED")
                    .creationDate(LocalDateTime.now())
                    .author(author)
                    .build();

            assertThat(bug).isEqualTo(bug2);
        }
    }

    @Nested
    @DisplayName("Comment Model Tests")
    class CommentModelTests {

        @Test
        @DisplayName("Equality should be based on id only")
        void equalityById() {
            User author = User.builder()
                    .id(1L)
                    .username("user")
                    .password("p")
                    .userRole(UserRole.USER)
                    .build();

            Comment c1 = Comment.builder()
                    .id(1L)
                    .text("Text 1")
                    .creationDate(LocalDateTime.now())
                    .author(author)
                    .build();

            Comment c2 = Comment.builder()
                    .id(1L)
                    .text("Completely Different")
                    .creationDate(LocalDateTime.now().plusHours(1))
                    .author(author)
                    .build();

            assertThat(c1).isEqualTo(c2);
            assertThat(c1.hashCode()).isEqualTo(c2.hashCode());
        }

        @Test
        @DisplayName("Comments with different ids should not be equal")
        void inequalityByDifferentId() {
            Comment c1 = Comment.builder().id(1L).text("a").creationDate(LocalDateTime.now()).build();
            Comment c2 = Comment.builder().id(2L).text("a").creationDate(LocalDateTime.now()).build();

            assertThat(c1).isNotEqualTo(c2);
        }
    }

    @Nested
    @DisplayName("Tag Model Tests")
    class TagModelTests {

        @Test
        @DisplayName("Should have empty bugs set by default")
        void defaultBugsSet() {
            Tag tag = Tag.builder()
                    .id(1L)
                    .name("backend")
                    .build();

            assertThat(tag.getBugs()).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("Equality should be based on id only")
        void equalityById() {
            Tag t1 = Tag.builder().id(1L).name("UI").build();
            Tag t2 = Tag.builder().id(1L).name("Backend").build();

            assertThat(t1).isEqualTo(t2);
        }
    }

    @Nested
    @DisplayName("UserRole Enum Tests")
    class UserRoleTests {

        @Test
        @DisplayName("Should have USER and MODERATOR values")
        void hasExpectedValues() {
            assertThat(UserRole.values()).containsExactly(UserRole.USER, UserRole.MODERATOR);
        }

        @Test
        @DisplayName("valueOf should work for valid role names")
        void valueOfWorks() {
            assertThat(UserRole.valueOf("USER")).isEqualTo(UserRole.USER);
            assertThat(UserRole.valueOf("MODERATOR")).isEqualTo(UserRole.MODERATOR);
        }

        @Test
        @DisplayName("valueOf should throw for invalid role name")
        void valueOfInvalid() {
            assertThatThrownBy(() -> UserRole.valueOf("ADMIN"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
