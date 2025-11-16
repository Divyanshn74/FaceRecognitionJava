package com.example.facerecog.controller;

import com.example.facerecog.model.User;
import com.example.facerecog.repository.UserRepository;
import com.example.facerecog.service.AttendanceService;
import com.example.facerecog.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    private final AttendanceService attendanceService;
    private final UserRepository userRepository;
    private final UserService userService;

    public UserController(AttendanceService attendanceService, UserRepository userRepository, UserService userService) {
        this.attendanceService = attendanceService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/attendance")
    public String attendancePage() {
        return "student_attendance";
    }

    @GetMapping("/api/my-attendance")
    public ResponseEntity<?> getMyAttendance(Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        if (user.getEnrollmentNumber() == null || user.getEnrollmentNumber().isBlank()) {
            return ResponseEntity.status(404).body(Map.of("message", "Your user account is not linked to a student enrollment number."));
        }

        return ResponseEntity.ok(attendanceService.getStudentAttendance(user.getEnrollmentNumber()));
    }

    @GetMapping("/profile")
    public String profilePage(Model model, Principal principal) {
        userRepository.findByEmail(principal.getName())
                .ifPresent(user -> model.addAttribute("user", user));
        return "profile";
    }

    @PostMapping("/change-password")
    public String changePassword(Principal principal,
                                 @RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 RedirectAttributes redirectAttributes) {

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New passwords do not match.");
            return "redirect:/user/profile";
        }

        boolean success = userService.changeUserPassword(principal.getName(), currentPassword, newPassword);

        if (success) {
            redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Incorrect current password.");
        }

        return "redirect:/user/profile";
    }
}
