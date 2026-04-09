package com.bug.bug_reporter.controller;

import com.bug.bug_reporter.utility.EmailAlreadyExistsException;
import com.bug.bug_reporter.utility.UserAlreadyExistsException;
import com.bug.bug_reporter.utility.UserNotFoundException;
import com.bug.bug_reporter.utility.UsernameAlreadyExistsException;
import jakarta.validation.constraints.Email;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Conflict");
        error.put("message", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound(UserNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Not found");
        error.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleUsernameAlreadyExists(UsernameAlreadyExistsException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "conflict");
        error.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }


    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "conflict");
        error.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}