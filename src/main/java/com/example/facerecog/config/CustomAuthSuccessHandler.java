package com.example.facerecog.config;

import com.example.facerecog.model.User;
import com.example.facerecog.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    public CustomAuthSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user != null && user.isDefaultPasswordUsed()) {
            response.sendRedirect("/change-password");
        } else {
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MASTER"))) {
                response.sendRedirect("/master/dashboard");
            } else {
                response.sendRedirect("/");
            }
        }
    }
}
