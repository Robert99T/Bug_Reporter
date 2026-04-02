package com.bug.bug_reporter.service;

import com.bug.bug_reporter.dto.BugResponse;
import com.bug.bug_reporter.dto.CreateBugRequest;
import com.bug.bug_reporter.dto.UpdateBugRequest;
import com.bug.bug_reporter.model.Bug;
import com.bug.bug_reporter.model.User;
import com.bug.bug_reporter.model.UserRole;
import com.bug.bug_reporter.repository.BugRepository;
import com.bug.bug_reporter.repository.UserRepository;
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
class BugServiceTest {

    @Mock
    private BugRepository bugRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BugService bugService;

    private User testAuthor;
    private Bug testBug;

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
                .text("The login button does not respond on click")
                .status("OPEN")
                .creationDate(LocalDateTime.of(2026, 4, 1, 10, 0))
                .pictureUrl("http://images.example.com/bug1.png")
                .author(testAuthor)
                .build();
    }

    // ==================== createBug ====================

    @Test
    void createBug_shouldReturnBugResponse() {
        CreateBugRequest request = new CreateBugRequest();
        request.setTitle("Login button broken");
        request.setText("The login button does not respond on click");
        request.setStatus("OPEN");
        request.setPictureUrl("http://images.example.com/bug1.png");
        request.setAuthorId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testAuthor));
        when(bugRepository.save(any(Bug.class))).thenReturn(testBug);

        BugResponse result = bugService.createBug(request);

        assertNotNull(result);
        assertEquals("Login button broken", result.getTitle());
        assertEquals("OPEN", result.getStatus());
        assertEquals(1L, result.getAuthorId());
        assertEquals("john_doe", result.getAuthorUsername());

        verify(userRepository).findById(1L);
        verify(bugRepository).save(any(Bug.class));
    }

    @Test
    void createBug_userNotFound_shouldThrowException() {
        CreateBugRequest request = new CreateBugRequest();
        request.setAuthorId(99L);
        request.setTitle("Some bug");
        request.setText("Some text");
        request.setStatus("OPEN");

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bugService.createBug(request));

        verify(bugRepository, never()).save(any(Bug.class));
    }

    // ==================== getAllBugs ====================

    @Test
    void getAllBugs_shouldReturnListOfBugResponses() {
        Bug secondBug = Bug.builder()
                .id(2L)
                .title("Crash on upload")
                .text("Application crashes when uploading large files")
                .status("IN_PROGRESS")
                .creationDate(LocalDateTime.now())
                .author(testAuthor)
                .build();

        when(bugRepository.findAll()).thenReturn(List.of(testBug, secondBug));

        List<BugResponse> result = bugService.getAllBugs();

        assertEquals(2, result.size());
        assertEquals("Login button broken", result.get(0).getTitle());
        assertEquals("Crash on upload", result.get(1).getTitle());
    }

    @Test
    void getAllBugs_emptyList_shouldReturnEmptyList() {
        when(bugRepository.findAll()).thenReturn(List.of());

        List<BugResponse> result = bugService.getAllBugs();

        assertTrue(result.isEmpty());
    }

    // ==================== getBugById ====================

    @Test
    void getBugById_existingId_shouldReturnBugResponse() {
        when(bugRepository.findById(1L)).thenReturn(Optional.of(testBug));

        BugResponse result = bugService.getBugById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Login button broken", result.getTitle());
    }

    @Test
    void getBugById_nonExistingId_shouldThrowException() {
        when(bugRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bugService.getBugById(99L));
    }

    // ==================== updateBug ====================

    @Test
    void updateBug_allFields_shouldReturnUpdatedBugResponse() {
        UpdateBugRequest request = new UpdateBugRequest();
        request.setTitle("Login button fixed");
        request.setText("Fixed the click handler");
        request.setStatus("RESOLVED");
        request.setPictureUrl("http://images.example.com/bug1-fixed.png");

        Bug updatedBug = Bug.builder()
                .id(1L)
                .title("Login button fixed")
                .text("Fixed the click handler")
                .status("RESOLVED")
                .creationDate(testBug.getCreationDate())
                .pictureUrl("http://images.example.com/bug1-fixed.png")
                .author(testAuthor)
                .build();

        when(bugRepository.findById(1L)).thenReturn(Optional.of(testBug));
        when(bugRepository.save(any(Bug.class))).thenReturn(updatedBug);

        BugResponse result = bugService.updateBug(1L, request);

        assertNotNull(result);
        assertEquals("Login button fixed", result.getTitle());
        assertEquals("RESOLVED", result.getStatus());
    }

    @Test
    void updateBug_partialFields_shouldOnlyUpdateProvidedFields() {
        UpdateBugRequest request = new UpdateBugRequest();
        request.setStatus("CLOSED");
        // title, text, pictureUrl are null → should stay unchanged

        when(bugRepository.findById(1L)).thenReturn(Optional.of(testBug));
        when(bugRepository.save(any(Bug.class))).thenReturn(testBug);

        BugResponse result = bugService.updateBug(1L, request);

        // The original title should remain since request.title was null
        assertEquals("Login button broken", result.getTitle());
        verify(bugRepository).save(any(Bug.class));
    }

    @Test
    void updateBug_nonExistingId_shouldThrowException() {
        UpdateBugRequest request = new UpdateBugRequest();
        request.setTitle("Updated");

        when(bugRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bugService.updateBug(99L, request));

        verify(bugRepository, never()).save(any(Bug.class));
    }

    // ==================== deleteBug ====================

    @Test
    void deleteBug_existingId_shouldDeleteSuccessfully() {
        when(bugRepository.findById(1L)).thenReturn(Optional.of(testBug));
        doNothing().when(bugRepository).delete(testBug);

        assertDoesNotThrow(() -> bugService.deleteBug(1L));

        verify(bugRepository).delete(testBug);
    }

    @Test
    void deleteBug_nonExistingId_shouldThrowException() {
        when(bugRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bugService.deleteBug(99L));

        verify(bugRepository, never()).delete(any(Bug.class));
    }
}
