package com.bug.bug_reporter.service;

import com.bug.bug_reporter.dto.CommentResponse;
import com.bug.bug_reporter.dto.CreateCommentRequest;
import com.bug.bug_reporter.dto.UpdateCommentRequest;
import com.bug.bug_reporter.model.Bug;
import com.bug.bug_reporter.model.Comment;
import com.bug.bug_reporter.model.User;
import com.bug.bug_reporter.model.UserRole;
import com.bug.bug_reporter.repository.BugRepository;
import com.bug.bug_reporter.repository.CommentRepository;
import com.bug.bug_reporter.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BugRepository bugRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentService commentService;

    private User testAuthor;
    private Bug testBug;
    private Comment testComment;
    private CreateCommentRequest createRequest;
    private UpdateCommentRequest updateRequest;

    @BeforeEach
    void setUp() {
        testAuthor = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encoded")
                .userRole(UserRole.USER)
                .build();

        testBug = Bug.builder()
                .id(10L)
                .title("Test Bug")
                .text("Bug description")
                .status("OPEN")
                .creationDate(LocalDateTime.of(2026, 4, 1, 10, 0))
                .author(testAuthor)
                .build();

        testComment = Comment.builder()
                .id(100L)
                .text("This is a test comment")
                .pictureUrl("http://example.com/comment.png")
                .creationDate(LocalDateTime.of(2026, 4, 1, 12, 0))
                .author(testAuthor)
                .bug(testBug)
                .build();

        createRequest = new CreateCommentRequest();
        createRequest.setText("This is a test comment");
        createRequest.setPictureUrl("http://example.com/comment.png");
        createRequest.setAuthorId(1L);

        updateRequest = new UpdateCommentRequest();
        updateRequest.setText("Updated comment text");
        updateRequest.setPictureUrl("http://example.com/updated.png");
    }

    @Nested
    @DisplayName("createComment Tests")
    class CreateCommentTests {

        @Test
        @DisplayName("Should create a comment successfully")
        void createComment_Success() {
            // Arrange
            when(bugRepository.findById(10L)).thenReturn(Optional.of(testBug));
            when(userRepository.findById(1L)).thenReturn(Optional.of(testAuthor));
            when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

            // Act
            CommentResponse result = commentService.createComment(10L, createRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(100L);
            assertThat(result.getText()).isEqualTo("This is a test comment");
            assertThat(result.getAuthorId()).isEqualTo(1L);
            assertThat(result.getAuthorUsername()).isEqualTo("testuser");
            assertThat(result.getBugId()).isEqualTo(10L);

            verify(bugRepository).findById(10L);
            verify(userRepository).findById(1L);
            verify(commentRepository).save(any(Comment.class));
        }

        @Test
        @DisplayName("Should throw exception when bug not found")
        void createComment_BugNotFound() {
            // Arrange
            when(bugRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> commentService.createComment(99L, createRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Bug not found");

            verify(commentRepository, never()).save(any(Comment.class));
        }

        @Test
        @DisplayName("Should throw exception when author not found")
        void createComment_AuthorNotFound() {
            // Arrange
            when(bugRepository.findById(10L)).thenReturn(Optional.of(testBug));
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> commentService.createComment(10L, createRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("User not found");

            verify(commentRepository, never()).save(any(Comment.class));
        }
    }

    @Nested
    @DisplayName("getCommentsByBugId Tests")
    class GetCommentsByBugIdTests {

        @Test
        @DisplayName("Should return comments for a bug")
        void getCommentsByBugId_Success() {
            // Arrange
            Comment comment2 = Comment.builder()
                    .id(101L)
                    .text("Second comment")
                    .creationDate(LocalDateTime.now())
                    .author(testAuthor)
                    .bug(testBug)
                    .build();

            when(commentRepository.findByBugId(10L)).thenReturn(List.of(testComment, comment2));

            // Act
            List<CommentResponse> result = commentService.getCommentsByBugId(10L);

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getId()).isEqualTo(100L);
            assertThat(result.get(1).getId()).isEqualTo(101L);
            assertThat(result.get(0).getBugId()).isEqualTo(10L);
        }

        @Test
        @DisplayName("Should return empty list when no comments for bug")
        void getCommentsByBugId_Empty() {
            // Arrange
            when(commentRepository.findByBugId(10L)).thenReturn(Collections.emptyList());

            // Act
            List<CommentResponse> result = commentService.getCommentsByBugId(10L);

            // Assert
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getCommentById Tests")
    class GetCommentByIdTests {

        @Test
        @DisplayName("Should return comment when found")
        void getCommentById_Success() {
            // Arrange
            when(commentRepository.findById(100L)).thenReturn(Optional.of(testComment));

            // Act
            CommentResponse result = commentService.getCommentById(100L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(100L);
            assertThat(result.getText()).isEqualTo("This is a test comment");
            assertThat(result.getPictureUrl()).isEqualTo("http://example.com/comment.png");
        }

        @Test
        @DisplayName("Should throw exception when comment not found")
        void getCommentById_NotFound() {
            // Arrange
            when(commentRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> commentService.getCommentById(999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Comment not found");
        }
    }

    @Nested
    @DisplayName("updateComment Tests")
    class UpdateCommentTests {

        @Test
        @DisplayName("Should update comment text and pictureUrl")
        void updateComment_AllFields() {
            // Arrange
            when(commentRepository.findById(100L)).thenReturn(Optional.of(testComment));
            when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

            // Act
            CommentResponse result = commentService.updateComment(100L, updateRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(testComment.getText()).isEqualTo("Updated comment text");
            assertThat(testComment.getPictureUrl()).isEqualTo("http://example.com/updated.png");
            verify(commentRepository).save(testComment);
        }

        @Test
        @DisplayName("Should only update text when pictureUrl is null")
        void updateComment_OnlyText() {
            // Arrange
            UpdateCommentRequest textOnlyRequest = new UpdateCommentRequest();
            textOnlyRequest.setText("New text only");

            when(commentRepository.findById(100L)).thenReturn(Optional.of(testComment));
            when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

            // Act
            commentService.updateComment(100L, textOnlyRequest);

            // Assert
            assertThat(testComment.getText()).isEqualTo("New text only");
            assertThat(testComment.getPictureUrl()).isEqualTo("http://example.com/comment.png"); // unchanged
        }

        @Test
        @DisplayName("Should not update blank text")
        void updateComment_BlankTextIgnored() {
            // Arrange
            UpdateCommentRequest blankTextReq = new UpdateCommentRequest();
            blankTextReq.setText("   ");

            when(commentRepository.findById(100L)).thenReturn(Optional.of(testComment));
            when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

            // Act
            commentService.updateComment(100L, blankTextReq);

            // Assert
            assertThat(testComment.getText()).isEqualTo("This is a test comment"); // unchanged
        }

        @Test
        @DisplayName("Should throw exception when comment to update not found")
        void updateComment_NotFound() {
            // Arrange
            when(commentRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> commentService.updateComment(999L, updateRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Comment not found");
        }
    }

    @Nested
    @DisplayName("deleteComment Tests")
    class DeleteCommentTests {

        @Test
        @DisplayName("Should delete comment successfully")
        void deleteComment_Success() {
            // Arrange
            when(commentRepository.findById(100L)).thenReturn(Optional.of(testComment));
            doNothing().when(commentRepository).delete(testComment);

            // Act
            commentService.deleteComment(100L);

            // Assert
            verify(commentRepository).delete(testComment);
        }

        @Test
        @DisplayName("Should throw exception when comment to delete not found")
        void deleteComment_NotFound() {
            // Arrange
            when(commentRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> commentService.deleteComment(999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Comment not found");

            verify(commentRepository, never()).delete(any(Comment.class));
        }
    }
}
