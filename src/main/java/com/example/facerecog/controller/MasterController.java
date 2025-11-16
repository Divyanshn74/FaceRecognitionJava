package com.example.facerecog.controller;

import com.example.facerecog.model.Attendance;
import com.example.facerecog.model.Student;
import com.example.facerecog.model.User;
import com.example.facerecog.repository.StudentRepository;
import com.example.facerecog.repository.UserRepository;
import com.example.facerecog.service.AttendanceService;
import com.example.facerecog.service.SettingService;
import com.example.facerecog.service.StudentService;
import com.example.facerecog.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/master")
public class MasterController {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final StudentService studentService;
    private final UserService userService;
    private final AttendanceService attendanceService;
    private final SettingService settingService;

    public MasterController(UserRepository userRepository, StudentRepository studentRepository, PasswordEncoder passwordEncoder, StudentService studentService, UserService userService, AttendanceService attendanceService, SettingService settingService) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.studentService = studentService;
        this.userService = userService;
        this.attendanceService = attendanceService;
        this.settingService = settingService;
    }

    @GetMapping("/dashboard")
    public String masterDashboard() {
        return "master-dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "master-users";
    }

    @PostMapping("/update-user")
    public String updateUser(@RequestParam("userId") Long userId,
                             @RequestParam("name") String name,
                             @RequestParam("email") String email,
                             @RequestParam("role") String role,
                             @RequestParam(name = "enrollmentNumber", required = false) String enrollmentNumber) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setName(name);
            user.setEmail(email);
            user.setRole(role);
            if (enrollmentNumber != null && !enrollmentNumber.isBlank()) {
                user.setEnrollmentNumber(enrollmentNumber);
            } else {
                user.setEnrollmentNumber(null);
            }
            userRepository.save(user);
        });
        return "redirect:/master/users";
    }

    @PostMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/master/users";
    }

    @GetMapping("/add-user")
    public String addUserForm() {
        return "master-add-user";
    }

    @PostMapping("/save-user")
    public String saveUser(@ModelAttribute User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/master/users";
    }

    @GetMapping("/bulk-import-users")
    public String bulkImportUsersForm() {
        return "bulk-import-users";
    }

    @PostMapping("/bulk-import-users")
    public String bulkImportUsers(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please select a CSV file to upload.");
            return "redirect:/master/bulk-import-users";
        }
        try {
            userService.importUsersFromCsv(file);
            redirectAttributes.addFlashAttribute("success", "Users imported successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while processing the CSV file: " + e.getMessage());
        }
        return "redirect:/master/users";
    }

    @GetMapping("/students")
    public String manageStudents(Model model) {
        List<Student> students = studentRepository.findAll();
        model.addAttribute("students", students);
        return "master-students";
    }

    @GetMapping("/bulk-import-students")
    public String bulkImportStudentsForm() {
        return "bulk-import-students";
    }

    @PostMapping("/bulk-import-students")
    public String bulkImportStudents(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please select a CSV file to upload.");
            return "redirect:/master/bulk-import-students";
        }
        try {
            studentService.importStudentsFromCsv(file);
            redirectAttributes.addFlashAttribute("success", "Students imported successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while processing the CSV file: " + e.getMessage());
        }
        return "redirect:/master/students";
    }

    @GetMapping("/register-faces")
    public String registerFaces(Model model) {
        List<Student> students = studentRepository.findAll();
        model.addAttribute("students", students);
        return "register-faces";
    }

    @GetMapping("/edit-student/{id}")
    public String editStudentForm(@PathVariable Long id, Model model) {
        studentRepository.findById(id).ifPresent(student -> model.addAttribute("student", student));
        return "master-edit-student";
    }

    @PostMapping("/update-student")
    public String updateStudent(@ModelAttribute Student student, @RequestParam(name = "newFaceImage", required = false) String newFaceImage) {
        studentService.updateStudentAndFace(student, newFaceImage);
        return "redirect:/master/students";
    }

    @PostMapping("/delete-student/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentRepository.deleteById(id);
        return "redirect:/master/students";
    }

    @GetMapping("/settings")
    public String settingsPage(Model model) {
        model.addAttribute("settings", settingService.getAllSettings());
        return "master-settings";
    }

    @PostMapping("/settings")
    public String updateSettings(@RequestParam Map<String, String> allParams, RedirectAttributes redirectAttributes) {
        allParams.forEach(settingService::updateSetting);
        redirectAttributes.addFlashAttribute("success", "Settings updated successfully!");
        return "redirect:/master/settings";
    }

    @GetMapping("/global-attendance")
    public String globalAttendance(@RequestParam(name = "enrollmentNumber", required = false) String enrollmentNumber,
                                   @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                   @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                   Model model) {
        List<Attendance> records = attendanceService.searchAttendance(enrollmentNumber, startDate, endDate);
        model.addAttribute("attendanceRecords", records);
        model.addAttribute("enrollmentNumber", enrollmentNumber);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "global-attendance";
    }

    @PostMapping("/update-attendance")
    @ResponseBody
    public ResponseEntity<?> updateAttendance(@RequestParam("attendanceId") Long attendanceId,
                                              @RequestParam("status") String status) {
        try {
            attendanceService.updateAttendanceStatus(attendanceId, status);
            return ResponseEntity.ok(Map.of("message", "Attendance record updated successfully."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "An error occurred: " + e.getMessage()));
        }
    }
}
