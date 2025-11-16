package com.example.facerecog.config;

import com.example.facerecog.model.User;
import com.example.facerecog.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class PasswordChangeFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    public PasswordChangeFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String path = request.getRequestURI();

        if (authentication != null && authentication.isAuthenticated() && !path.equals("/change-password") && !path.equals("/logout") && !path.startsWith("/css")) {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email).orElse(null);

            if (user != null && user.isDefaultPasswordUsed()) {
                response.sendRedirect("/change-password");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
