package com.bug.bug_reporter.service;

import com.bug.bug_reporter.dto.BugResponse;
import com.bug.bug_reporter.dto.CreateBugRequest;
import com.bug.bug_reporter.dto.UpdateBugRequest;
import com.bug.bug_reporter.model.Bug;
import com.bug.bug_reporter.model.Comment;
import com.bug.bug_reporter.model.User;
import com.bug.bug_reporter.model.UserRole;
import com.bug.bug_reporter.repository.BugRepository;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BugServiceTest {

    @Mock
    private BugRepository bugRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BugService bugService;

    private User testAuthor;
    private Bug testBug;
    private CreateBugRequest createRequest;
    private UpdateBugRequest updateRequest;

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
                .id(1L)
                .title("Test Bug")
                .text("This is a test bug description")
                .pictureUrl("http://example.com/image.png")
                .status("OPEN")
                .creationDate(LocalDateTime.of(2026, 4, 1, 10, 0))
                .author(testAuthor)
                .comments(new ArrayList<>())
                .build();

        createRequest = new CreateBugRequest();
        createRequest.setTitle("Test Bug");
        createRequest.setText("This is a test bug description");
        createRequest.setPictureUrl("http://example.com/image.png");
        createRequest.setStatus("OPEN");
        createRequest.setAuthorId(1L);

        updateRequest = new UpdateBugRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setText("Updated text");
        updateRequest.setStatus("IN_PROGRESS");
    }

    @Nested
    @DisplayName("createBug Tests")
    class CreateBugTests {

        @Test
        @DisplayName("Should create a bug successfully")
        void createBug_Success() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testAuthor));
            when(bugRepository.save(any(Bug.class))).thenReturn(testBug);

            // Act
            BugResponse result = bugService.createBug(createRequest);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getTitle()).isEqualTo("Test Bug");
            assertThat(result.getText()).isEqualTo("This is a test bug description");
            assertThat(result.getStatus()).isEqualTo("OPEN");
            assertThat(result.getAuthorId()).isEqualTo(1L);
            assertThat(result.getAuthorUsername()).isEqualTo("testuser");

            verify(userRepository).findById(1L);
            verify(bugRepository).save(any(Bug.class));
        }

        @Test
        @DisplayName("Should throw exception when author not found")
        void createBug_AuthorNotFound() {
            // Arrange
            when(userRepository.findById(99L)).thenReturn(Optional.empty());
            createRequest.setAuthorId(99L);

            // Act & Assert
            assertThatThrownBy(() -> bugService.createBug(createRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("User not found");

            verify(bugRepository, never()).save(any(Bug.class));
        }
    }

    @Nested
    @DisplayName("getAllBugs Tests")
    class GetAllBugsTests {

        @Test
        @DisplayName("Should return all bugs")
        void getAllBugs_Success() {
            // Arrange
            Bug bug2 = Bug.builder()
                    .id(2L)
                    .title("Second Bug")
                    .text("Another bug")
                    .status("CLOSED")
                    .creationDate(LocalDateTime.now())
                    .author(testAuthor)
                    .comments(new ArrayList<>())
                    .build();

            when(bugRepository.findAll()).thenReturn(List.of(testBug, bug2));

            // Act
            List<BugResponse> result = bugService.getAllBugs();

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getTitle()).isEqualTo("Test Bug");
            assertThat(result.get(1).getTitle()).isEqualTo("Second Bug");
        }

        @Test
        @DisplayName("Should return empty list when no bugs exist")
        void getAllBugs_EmptyList() {
            // Arrange
            when(bugRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            List<BugResponse> result = bugService.getAllBugs();

            // Assert
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getBugById Tests")
    class GetBugByIdTests {

        @Test
        @DisplayName("Should return bug when found")
        void getBugById_Success() {
            // Arrange
            when(bugRepository.findById(1L)).thenReturn(Optional.of(testBug));

            // Act
            BugResponse result = bugService.getBugById(1L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getTitle()).isEqualTo("Test Bug");
            assertThat(result.getStatus()).isEqualTo("OPEN");
        }

        @Test
        @DisplayName("Should throw exception when bug not found")
        void getBugById_NotFound() {
            // Arrange
            when(bugRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> bugService.getBugById(99L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Bug not found");
        }

        @Test
        @DisplayName("Should include comments in bug response")
        void getBugById_WithComments() {
            // Arrange
            Comment comment = Comment.builder()
                    .id(1L)
                    .text("A comment")
                    .creationDate(LocalDateTime.now())
                    .author(testAuthor)
                    .bug(testBug)
                    .build();
            testBug.getComments().add(comment);

            when(bugRepository.findById(1L)).thenReturn(Optional.of(testBug));

            // Act
            BugResponse result = bugService.getBugById(1L);

            // Assert
            assertThat(result.getComments()).hasSize(1);
            assertThat(result.getComments().get(0).getText()).isEqualTo("A comment");
            assertThat(result.getComments().get(0).getAuthorUsername()).isEqualTo("testuser");
        }
    }

    @Nested
    @DisplayName("updateBug Tests")
    class UpdateBugTests {

        @Test
        @DisplayName("Should update all fields of bug")
        void updateBug_AllFields() {
            // Arrange
            when(bugRepository.findById(1L)).thenReturn(Optional.of(testBug));
            when(bugRepository.save(any(Bug.class))).thenReturn(testBug);

            // Act
            BugResponse result = bugService.updateBug(1L, updateRequest);

            // Assert
            assertThat(result).isNotNull();
            verify(bugRepository).save(testBug);
            assertThat(testBug.getTitle()).isEqualTo("Updated Title");
            assertThat(testBug.getText()).isEqualTo("Updated text");
            assertThat(testBug.getStatus()).isEqualTo("IN_PROGRESS");
        }

        @Test
        @DisplayName("Should only update provided fields (partial update)")
        void updateBug_PartialUpdate() {
            // Arrange
            UpdateBugRequest partialRequest = new UpdateBugRequest();
            partialRequest.setTitle("Only Title Updated");
            // text, pictureUrl, status are null

            when(bugRepository.findById(1L)).thenReturn(Optional.of(testBug));
            when(bugRepository.save(any(Bug.class))).thenReturn(testBug);

            // Act
            bugService.updateBug(1L, partialRequest);

            // Assert — title changes, others stay the same
            assertThat(testBug.getTitle()).isEqualTo("Only Title Updated");
            assertThat(testBug.getText()).isEqualTo("This is a test bug description");
            assertThat(testBug.getStatus()).isEqualTo("OPEN");
        }

        @Test
        @DisplayName("Should not update blank title")
        void updateBug_BlankTitleIgnored() {
            // Arrange
            UpdateBugRequest blankTitleRequest = new UpdateBugRequest();
            blankTitleRequest.setTitle("   ");

            when(bugRepository.findById(1L)).thenReturn(Optional.of(testBug));
            when(bugRepository.save(any(Bug.class))).thenReturn(testBug);

            // Act
            bugService.updateBug(1L, blankTitleRequest);

            // Assert
            assertThat(testBug.getTitle()).isEqualTo("Test Bug"); // unchanged
        }

        @Test
        @DisplayName("Should throw exception when bug to update not found")
        void updateBug_NotFound() {
            // Arrange
            when(bugRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> bugService.updateBug(99L, updateRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Bug not found");
        }
    }

    @Nested
    @DisplayName("deleteBug Tests")
    class DeleteBugTests {

        @Test
        @DisplayName("Should delete bug successfully")
        void deleteBug_Success() {
            // Arrange
            when(bugRepository.findById(1L)).thenReturn(Optional.of(testBug));
            doNothing().when(bugRepository).delete(testBug);

            // Act
            bugService.deleteBug(1L);

            // Assert
            verify(bugRepository).delete(testBug);
        }

        @Test
        @DisplayName("Should throw exception when bug to delete not found")
        void deleteBug_NotFound() {
            // Arrange
            when(bugRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> bugService.deleteBug(99L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Bug not found");

            verify(bugRepository, never()).delete(any(Bug.class));
        }
    }
}
