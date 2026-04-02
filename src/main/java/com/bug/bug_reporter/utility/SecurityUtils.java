package com.bug.bug_reporter.utility;

import com.bug.bug_reporter.security.CustomUserDetails;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    private static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getId();
        }

        throw new IllegalStateException("Principal is not an instance of CustomUserDetails");
    }

    public static boolean hasPermission(Long id) {
        return getCurrentUserId().equals(id);
    }
}
