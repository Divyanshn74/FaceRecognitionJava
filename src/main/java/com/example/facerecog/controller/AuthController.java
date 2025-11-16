package com.example.facerecog.controller;

import com.example.facerecog.model.Student;
import com.example.facerecog.model.User;
import com.example.facerecog.repository.StudentRepository;
import com.example.facerecog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() &&
            authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))) {
            
            String email = authentication.getName();
            Optional<User> userOptional = userRepository.findByEmail(email);

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                String enrollmentNumber = user.getEnrollmentNumber();
                if (enrollmentNumber != null && !enrollmentNumber.isBlank()) {
                    Optional<Student> studentOptional = studentRepository.findByEnrollmentNumber(enrollmentNumber);
                    if (studentOptional.isPresent()) {
                        Student student = studentOptional.get();
                        model.addAttribute("rollNumber", student.getRollNo());
                        model.addAttribute("enrollmentNumber", student.getEnrollmentNumber());
                    } else {
                        model.addAttribute("rollNumber", "NA");
                        model.addAttribute("enrollmentNumber", "NA");
                    }
                } else {
                    model.addAttribute("rollNumber", "NA");
                    model.addAttribute("enrollmentNumber", "NA");
                }
            }
        }
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/doRegister")
    public String doRegister(@ModelAttribute User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER"); // default
        userRepository.save(user);
        return "redirect:/login";
    }

    @GetMapping("/default")
    public String defaultAfterLogin(Authentication authentication) {
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MASTER"))) {
            return "redirect:/master/dashboard";
        }
        // Redirect all other users to the index page after login.
        return "redirect:/";
    }

    @GetMapping("/error/403")
    public String accessDenied() {
        return "error/403";
    }
}
