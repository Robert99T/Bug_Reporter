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
import com.bug.bug_reporter.repository.BugVoteRepository;
import com.bug.bug_reporter.repository.CommentVoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    @Mock
    private BugVoteRepository bugVoteRepository;

    @Mock
    private CommentVoteRepository commentVoteRepository;

    @InjectMocks
    private CommentService commentService;

    private User testAuthor;
    private Bug testBug;
    private Comment testComment;

    @BeforeEach
    void setUp() {
        testAuthor = User.builder()
                .id(1L)
                .username("john_doe")
                .email("john@example.com")
                .password("encodedPassword")
                .userRole(UserRole.USER)
                .build();

        testBug = Bug.builder()
                .id(1L)
                .title("Login button broken")
                .text("Doesn't respond on click")
                .status("OPEN")
                .creationDate(LocalDateTime.of(2026, 4, 1, 10, 0))
                .author(testAuthor)
                .build();

        testComment = Comment.builder()
                .id(1L)
                .text("I can reproduce this issue")
                .pictureUrl("http://images.example.com/screenshot.png")
                .creationDate(LocalDateTime.of(2026, 4, 1, 12, 0))
                .author(testAuthor)
                .bug(testBug)
                .build();
    }

    // ==================== createComment ====================

    @Test
    void createComment_shouldReturnCommentResponse() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setText("I can reproduce this issue");
        request.setPictureUrl("http://images.example.com/screenshot.png");
        request.setAuthorId(1L);

        when(bugRepository.findById(1L)).thenReturn(Optional.of(testBug));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testAuthor));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        CommentResponse result = commentService.createComment(1L, request);

        assertNotNull(result);
        assertEquals("I can reproduce this issue", result.getText());
        assertEquals(1L, result.getAuthorId());
        assertEquals("john_doe", result.getAuthorUsername());
        assertEquals(1L, result.getBugId());

        verify(bugRepository).findById(1L);
        verify(userRepository).findById(1L);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void createComment_bugNotFound_shouldThrowException() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setText("Some comment");
        request.setAuthorId(1L);

        when(bugRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> commentService.createComment(99L, request));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void createComment_userNotFound_shouldThrowException() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setText("Some comment");
        request.setAuthorId(99L);

        when(bugRepository.findById(1L)).thenReturn(Optional.of(testBug));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> commentService.createComment(1L, request));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    // ==================== getCommentsByBugId ====================

    @Test
    void getCommentsByBugId_shouldReturnListOfCommentResponses() {
        Comment secondComment = Comment.builder()
                .id(2L)
                .text("Same here, confirmed on Firefox")
                .creationDate(LocalDateTime.now())
                .author(testAuthor)
                .bug(testBug)
                .build();

        when(commentRepository.findByBugId(1L)).thenReturn(List.of(testComment, secondComment));

        List<CommentResponse> result = commentService.getCommentsByBugId(1L, null);

        assertEquals(2, result.size());
        assertEquals("I can reproduce this issue", result.get(0).getText());
        assertEquals("Same here, confirmed on Firefox", result.get(1).getText());
    }

    @Test
    void getCommentsByBugId_noComments_shouldReturnEmptyList() {
        when(commentRepository.findByBugId(1L)).thenReturn(List.of());

        List<CommentResponse> result = commentService.getCommentsByBugId(1L, null);

        assertTrue(result.isEmpty());
    }

    // ==================== getCommentById ====================

    @Test
    void getCommentById_existingId_shouldReturnCommentResponse() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));

        CommentResponse result = commentService.getCommentById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("I can reproduce this issue", result.getText());
    }

    @Test
    void getCommentById_nonExistingId_shouldThrowException() {
        when(commentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> commentService.getCommentById(99L));
    }

    // ==================== updateComment ====================

    @Test
    void updateComment_shouldReturnUpdatedCommentResponse() {
        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setText("Updated: confirmed on all browsers");
        request.setPictureUrl("http://images.example.com/new-screenshot.png");

        Comment updatedComment = Comment.builder()
                .id(1L)
                .text("Updated: confirmed on all browsers")
                .pictureUrl("http://images.example.com/new-screenshot.png")
                .creationDate(testComment.getCreationDate())
                .author(testAuthor)
                .bug(testBug)
                .build();

        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(updatedComment);

        CommentResponse result = commentService.updateComment(1L, request);

        assertNotNull(result);
        assertEquals("Updated: confirmed on all browsers", result.getText());
        assertEquals("http://images.example.com/new-screenshot.png", result.getPictureUrl());
    }

    @Test
    void updateComment_nonExistingId_shouldThrowException() {
        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setText("Updated text");

        when(commentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> commentService.updateComment(99L, request));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    // ==================== deleteComment ====================

    @Test
    void deleteComment_existingId_shouldDeleteSuccessfully() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        doNothing().when(commentRepository).delete(testComment);

        assertDoesNotThrow(() -> commentService.deleteComment(1L));

        verify(commentRepository).delete(testComment);
    }

    @Test
    void deleteComment_nonExistingId_shouldThrowException() {
        when(commentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> commentService.deleteComment(99L));

        verify(commentRepository, never()).delete(any(Comment.class));
    }
}
