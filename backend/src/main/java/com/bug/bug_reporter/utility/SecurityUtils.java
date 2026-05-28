package com.bug.bug_reporter.utility;

import com.bug.bug_reporter.security.CustomUserDetails;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static Long getCurrentUserId() {
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

    public static boolean isModerator() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return false;
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MODERATOR"));
    }

    public static boolean hasPermissionOrIsModerator(Long ownerId) {
        return hasPermission(ownerId) || isModerator();
    }
}

