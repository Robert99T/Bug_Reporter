package com.bug.bug_reporter.security;

import com.bug.bug_reporter.model.User;
import com.bug.bug_reporter.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Intercepts every authenticated request to check if the user has been banned
 * since their session was created. If banned, the session is invalidated and
 * a 403 ACCOUNT_BANNED response is returned.
 */
@Component
@RequiredArgsConstructor
public class BannedUserFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {

            Optional<User> userOpt = userRepository.findById(userDetails.getId());

            if (userOpt.isPresent() && userOpt.get().isBanned()) {
                // Invalidate the session
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                }
                SecurityContextHolder.clearContext();

                // Return 403 with ban message
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                // Write the raw JSON string directly to bypass Jackson dependency issues
                String jsonError = "{\"error\": \"ACCOUNT_BANNED\", \"message\": \"Your account has been banned. Please contact an administrator.\"}";
                response.getWriter().write(jsonError);

                return; // Do NOT continue the filter chain
            }
        }

        filterChain.doFilter(request, response);
    }
}