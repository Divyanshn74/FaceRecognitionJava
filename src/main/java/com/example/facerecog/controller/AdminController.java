package com.example.facerecog.controller;

import com.example.facerecog.dto.StudentAttendanceDTO;
import com.example.facerecog.model.Attendance;
import com.example.facerecog.model.Student;
import com.example.facerecog.repository.StudentRepository;
import com.example.facerecog.service.AttendanceService;
import com.example.facerecog.service.PdfService;
import com.example.facerecog.service.StudentService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final StudentService studentService;
    private final AttendanceService attendanceService;
    private final PdfService pdfService;
    private final StudentRepository studentRepository;

    public AdminController(StudentService studentService, AttendanceService attendanceService, PdfService pdfService, StudentRepository studentRepository) {
        this.studentService = studentService;
        this.attendanceService = attendanceService;
        this.pdfService = pdfService;
        this.studentRepository = studentRepository;
    }

    @GetMapping("/register-student")
    public String registerStudentForm() {
        return "student-register";
    }

    @GetMapping("/mark-attendance")
    public String markAttendance() {
        return "attendance";
    }

    @GetMapping("/manual-attendance")
    public String manualAttendancePage(@RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                       Model model) {
        LocalDate selectedDate = (date == null) ? LocalDate.now() : date;
        List<StudentAttendanceDTO> studentsWithStatus = attendanceService.getStudentAttendanceForDate(selectedDate);

        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("studentsWithStatus", studentsWithStatus);
        return "manual-attendance";
    }

    @PostMapping("/save-manual-attendance")
    public String saveManualAttendance(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                       StudentAttendanceListWrapper wrapper) {
        attendanceService.saveManualAttendance(date, wrapper.getStatuses());
        return "redirect:/admin/manual-attendance?date=" + date;
    }

    @GetMapping("/attendance-report")
    public String attendanceReportForm() {
        return "attendance-report";
    }

    @PostMapping("/generate-report")
    public ResponseEntity<InputStreamResource> generateReport(@RequestParam(name = "enrollmentNumber", required = false) String enrollmentNumber,
                                                              @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                              @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws IOException {
        List<Attendance> records = attendanceService.searchAttendance(enrollmentNumber, startDate, endDate);
        ByteArrayInputStream bis = pdfService.generateAttendancePdf(records, startDate, endDate);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=attendance-report.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    @GetMapping("/register-faces")
    public String registerFaces(Model model) {
        model.addAttribute("students", studentRepository.findAll());
        return "admin-register-faces";
    }

    @GetMapping("/edit-student/{id}")
    public String editStudentForm(@PathVariable Long id, Model model) {
        studentRepository.findById(id).ifPresent(student -> model.addAttribute("student", student));
        return "admin-edit-student";
    }

    @PostMapping("/update-student")
    public String updateStudent(@ModelAttribute Student student, @RequestParam(name = "newFaceImage", required = false) String newFaceImage) {
        studentService.updateStudentAndFace(student, newFaceImage);
        return "redirect:/admin/register-faces";
    }

    // Wrapper class to bind the list from the form
    public static class StudentAttendanceListWrapper {
        private List<StudentAttendanceDTO> statuses;

        public List<StudentAttendanceDTO> getStatuses() {
            return statuses;
        }

        public void setStatuses(List<StudentAttendanceDTO> statuses) {
            this.statuses = statuses;
        }
    }
}