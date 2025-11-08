package com.example.facerecog.controller;

import com.example.facerecog.dto.AttendanceRequest;
import com.example.facerecog.dto.AttendanceResponse;
import com.example.facerecog.dto.RegisterRequest;
import com.example.facerecog.service.AttendanceService;
import com.example.facerecog.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allow all origins for development, restrict in production
public class StudentController {

    private final StudentService studentService;
    private final AttendanceService attendanceService;

    public StudentController(StudentService studentService, AttendanceService attendanceService) {
        this.studentService = studentService;
        this.attendanceService = attendanceService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerStudent(@RequestBody RegisterRequest request) {
        System.out.println("Received registration request for rollNo: " + request.getRollNo());
        Map<String, String> response = studentService.registerStudent(request.getRollNo(), request.getName(), request.getFullName(), request.getEnrollmentNumber(), request.getImageBase64());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/attendance")
    public ResponseEntity<Map<String, String>> markAttendance(@RequestBody AttendanceRequest request) {
        System.out.println("Received attendance request for rollNo: " + request.getRollNo());
        Map<String, String> response = attendanceService.markAttendance(request.getRollNo(), request.getImageBase64());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/attendance/{enrollmentNumber}")
    public ResponseEntity<List<AttendanceResponse>> getStudentAttendance(@PathVariable String enrollmentNumber) {
        List<AttendanceResponse> attendance = attendanceService.getStudentAttendance(enrollmentNumber);
        return ResponseEntity.ok(attendance);
    }
}
